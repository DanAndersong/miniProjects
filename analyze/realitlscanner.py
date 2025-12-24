#!/usr/bin/env python3
"""
RealiTLScanner - Python порт оригинальной Go утилиты
Точное воспроизведение логики сканирования
"""

import argparse
import csv
import sys
import time
import signal
import socket

from typing import Optional
from concurrent.futures import ThreadPoolExecutor, as_completed
from threading import Event
# Импортируем дебаг из отдельного модуля
from debug_log import debug_log, set_debug  # не debug_utils!

from geo import Geo
from utils import Host, HostType, iterate_single_addr, iterate_file, fetch_domains_from_url
from scanner import scan_tls, TLSInfo

# Глобальные переменные для остановки по сигналу
stop_event = Event()


def signal_handler(sig, frame):
    print("\n[!] Received interrupt signal, stopping...")
    stop_event.set()
    sys.exit(0)

class RealiTLScanner:
    def __init__(self, args):
        self.args = args
        self.geo = Geo(args.geodb)
        self.output_file = None
        self.csv_writer = None
        
        if args.output:
            self.output_file = open(args.output, 'w', newline='', encoding='utf-8')
            self.csv_writer = csv.writer(self.output_file)
            self.csv_writer.writerow(['IP', 'ORIGIN', 'CERT_DOMAIN', 'CERT_ISSUER', 'GEO_CODE'])
    
    def close(self):
        if self.output_file:
            self.output_file.close()
        self.geo.close()
    
    def process_host(self, host: Host) -> Optional[TLSInfo]:
        target_ip = host.ip
        if not target_ip and host.type == HostType.DOMAIN:
            try:
                result = socket.getaddrinfo(host.origin, None)
                for family, _, _, _, sockaddr in result:
                    if family == socket.AF_INET or (self.args.enable_ipv6 and family == socket.AF_INET6):
                        target_ip = sockaddr[0]
                        break
            except:
                debug_log(f"Failed to resolve {host.origin}")
                return None
        
        if not target_ip:
            return None
        
        server_name = host.origin if host.type == HostType.DOMAIN else None
        result = scan_tls(target_ip, host.origin, self.args.port, self.args.timeout, server_name)
        
        if not result:
            return None
        
        geo_code = self.geo.get_country(target_ip)
        
        # ИЗМЕНЕНИЕ ЗДЕСЬ: проверяем флаг only_feasible
        if result.feasible:
            # Всегда показываем feasible сайты
            print(f"\033[92m[+] {target_ip:15} {host.origin:30} "
                f"{result.tls_version.value:8} {result.alpn_protocol or 'N/A':6} "
                f"{result.cert_domain:20} {geo_code}\033[0m")
            
            if self.csv_writer:
                self.csv_writer.writerow([
                    target_ip,
                    host.origin,
                    result.cert_domain,
                    f'"{result.cert_issuer}"',
                    geo_code
                ])
                self.output_file.flush()
        elif not self.args.only_feasible and self.args.verbose:
            # Показываем не-feasible ТОЛЬКО если:
            # 1. Флаг --only-feasible НЕ установлен
            # 2. И установлен флаг -v (verbose)
            print(f"[-] {target_ip:15} {host.origin:30} "
                f"{result.tls_version.value:8} {result.alpn_protocol or 'N/A':6} "
                f"{result.cert_domain:20} {geo_code}")
        
        return result
        
        # Сканируем TLS
        server_name = host.origin if host.type == HostType.DOMAIN else None
        result = scan_tls(
            target_ip, 
            host.origin, 
            self.args.port, 
            self.args.timeout,
            server_name
        )
        
        if not result:
            return None
        
        # Получаем геолокацию
        geo_code = self.geo.get_country(target_ip)
        
        # Логируем результат
        log_func = print
        if result.feasible:
            log_func = lambda msg: print(f"\033[92m{msg}\033[0m")  # Зеленый для feasible
            # Записываем в CSV
            if self.csv_writer:
                self.csv_writer.writerow([
                    target_ip,
                    host.origin,
                    result.cert_domain,
                    f'"{result.cert_issuer}"',
                    geo_code
                ])
                self.output_file.flush()
        else:
            if not self.args.verbose:
                return None  # Пропускаем не-feasible в не-verbose режиме
        
        # Форматированный вывод как в оригинале
        status = "[+]" if result.feasible else "[-]"
        print(f"{status} {target_ip:15} {host.origin:30} "
              f"{result.tls_version.value:8} {result.alpn_protocol or 'N/A':6} "
              f"{result.cert_domain:20} {geo_code}")
        
        return result
    
    def run(self):
        debug_log(f"RUN: Starting scanner")

        print(f"[*] RealiTLScanner started at {time.strftime('%H:%M:%S')}")
        print(f"[*] Threads: {self.args.threads}, Timeout: {self.args.timeout}s")
        print(f"[*] Port: {self.args.port}, IPv6: {'Enabled' if self.args.enable_ipv6 else 'Disabled'}")
        
        # Определяем источник хостов
        hosts_generator = None
        
        if self.args.addr:
            print(f"[*] Scanning from: {self.args.addr}")
            hosts_generator = iterate_single_addr(self.args.addr, self.args.enable_ipv6)
        elif hasattr(self.args, 'input_file') and self.args.input_file:
            print(f"[*] Reading from file: {self.args.input_file}")
            hosts_generator = iterate_file(self.args.input_file, self.args.enable_ipv6)
        elif self.args.url:
            print(f"[*] Fetching from URL: {self.args.url}")
            domains = fetch_domains_from_url(self.args.url)
            print(f"[*] Found {len(domains)} domains")
            # Создаем простой генератор из списка доменов
            hosts_generator = (Host(ip=None, origin=d, host_type=HostType.DOMAIN) for d in domains)
        
        if not hosts_generator:
            print("[-] No valid input source")
            return
        
        # Статистика
        start_time = time.time()
        last_print_time = start_time  # Для прогресса
        scanned_count = 0
        feasible_count = 0
        
        # Пул потоков для параллельного сканирования
        with ThreadPoolExecutor(max_workers=self.args.threads) as executor:
            futures = {}
            
            try:
                debug_log(f"RUN: Starting main loop")

                # Читаем хосты из генератора и отправляем в пул
                for host in hosts_generator:
                    debug_log(f"RUN: Got host from generator: {host.ip}")
                    if stop_event.is_set():
                        break
                    
                    # Ограничение по количеству (если указано)
                    if hasattr(self.args, 'limit') and self.args.limit and scanned_count >= self.args.limit:
                        print(f"[*] Reached limit of {self.args.limit} hosts")
                        break
                    # ОТЛАДКА: логируем состояние очереди
                    debug_log(f"RUN: Queue size: {len(futures)}, Scanned: {scanned_count}")
                    # Задержка если очередь слишком большая
                    while len(futures) >= self.args.threads:
                        debug_log(f"RUN: Queue full ({len(futures)}), waiting...")
                        time.sleep(0.5)
                        
                        # Очистка завершенных
                        for f in list(futures.keys()):
                            if f.done():
                                debug_log(f"RUN: Task completed for {futures[f].ip}")
                                try:
                                    result = f.result()
                                    if result and result.feasible:
                                        feasible_count += 1
                                except Exception as e:
                                    debug_log(f"RUN: Task error: {e}")
                                finally:
                                    del futures[f]
                    # Ограничение по времени (если указано)
                    if hasattr(self.args, 'max_time') and self.args.max_time and (time.time() - start_time) > self.args.max_time:
                        print(f"[*] Reached time limit of {self.args.max_time}s")
                        break
                    
                    # Показываем прогресс каждые 2 секунды
                    current_time = time.time()
                    if current_time - last_print_time > 2:
                        elapsed = current_time - start_time
                        print(f"[Progress] Scanned: {scanned_count}, Feasible: {feasible_count}, Time: {elapsed:.1f}s")
                        last_print_time = current_time
                    
                    # Отправляем задачу в пул
                    future = executor.submit(self.process_host, host)
                    futures[future] = host
                    scanned_count += 1
                    
                    # Очищаем завершенные задачи
                    for f in list(futures.keys()):
                        if f.done():
                            try:
                                result = f.result()
                                if result and result.feasible:
                                    feasible_count += 1
                            except:
                                pass
                            finally:
                                del futures[f]
                    
                    # Не создаем слишком много одновременных задач
                    while len(futures) >= self.args.threads * 2:
                        time.sleep(0.01)
                
                # Ждем завершения всех задач
                for future in as_completed(futures.keys()):
                    try:
                        result = future.result()
                        if result and result.feasible:
                            feasible_count += 1
                    except:
                        pass
                    
            except KeyboardInterrupt:
                print("\n[!] Scan interrupted")
            except Exception as e:
                print(f"[-] Error: {e}")
        
        # Вывод статистики
        elapsed = time.time() - start_time
        print(f"\n{'='*60}")
        print(f"[*] Scan completed at {time.strftime('%H:%M:%S')}")
        print(f"[*] Total scanned: {scanned_count}")
        print(f"[*] Feasible sites: {feasible_count}")
        print(f"[*] Time elapsed: {elapsed:.1f}s")
        
        if hasattr(self.args, 'output') and self.args.output:
            print(f"[*] Results saved to: {self.args.output}")
                    

def main():

    parser = argparse.ArgumentParser(
        description="RealiTLScanner - Find websites with TLS 1.3 and HTTP/2 support",
        formatter_class=argparse.RawDescriptionHelpFormatter
    )
    
    # Входные данные (взаимоисключающие)
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument("-addr", help="IP, CIDR or domain to scan")
    group.add_argument("-in", dest="input_file", help="Input file with IPs/CIDRs/domains")  # Изменено на input_file
    group.add_argument("-url", help="URL to crawl for domains")
    
    # Параметры сканирования
    parser.add_argument("-port", type=int, default=443, help="HTTPS port (default: 443)")
    parser.add_argument("-thread", type=int, default=2, dest="threads", help="Concurrent tasks (default: 2)")
    parser.add_argument("-out", dest="output", help="Output CSV file")  # Добавлено dest="output"
    parser.add_argument("-timeout", type=int, default=10, help="Timeout per check (default: 10)")
    parser.add_argument("-v", action="store_true", dest="verbose", help="Verbose output")
    parser.add_argument("-46", action="store_true", dest="enable_ipv6", help="Enable IPv6")
    parser.add_argument("--debug", action="store_true", help="Enable debug logging")
    parser.add_argument("-f", "--only-feasible", action="store_true", 
                       help="Show only feasible sites (TLS 1.3 + HTTP/2)")

    # Дополнительные опции для бесконечного режима
    parser.add_argument("-max-time", type=int, help="Maximum scanning time in seconds")
    parser.add_argument("-limit", type=int, help="Maximum hosts to scan")
    parser.add_argument("-geodb", default="Country.mmdb", help="GeoIP database path")
    
    args = parser.parse_args()
    
    set_debug(args.debug)  # Просто вызываем функцию

    # Регистрируем обработчик Ctrl+C
    signal.signal(signal.SIGINT, signal_handler)
    
    # Запускаем сканер
    scanner = RealiTLScanner(args)
    try:
        scanner.run()
    finally:
        scanner.close()

if __name__ == "__main__":
    main()