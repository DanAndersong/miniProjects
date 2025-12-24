import ipaddress
import re
import socket
import sys
import time
from debug_log import debug_log, set_debug  # не debug_utils!

from typing import Generator, List
from enum import Enum

class HostType(Enum):
    IP = 1
    CIDR = 2
    DOMAIN = 3

class Host:
    def __init__(self, ip=None, origin="", host_type=HostType.IP):
        self.ip = ip
        self.origin = origin
        self.type = host_type
    
    def __str__(self):
        return f"{self.ip} ({self.origin})"

def iterate_single_addr(addr: str, enable_ipv6=False) -> Generator[Host, None, None]:
    debug_log(f"GENERATOR: Starting for address: {addr}")

    """
    ТОЧНАЯ ЛОГИКА ОРИГИНАЛЬНОЙ Go-УТИЛИТЫ:
    1. Если это одиночный IP (не CIDR) или домен - БЕСКОНЕЧНЫЙ режим
    2. Если CIDR - разбирает все хосты и завершается
    """
    
    # Сначала пробуем как одиночный IP (не CIDR!)
    try:
        # Важно: ip_address() вернет ошибку для CIDR
        start_ip = ipaddress.ip_address(addr)
        
        # Проверяем IPv6
        if isinstance(start_ip, ipaddress.IPv6Address) and not enable_ipv6:
            return
        
        print(f"[+] Enable infinite mode, init: {start_ip}")
        
        # Сначала возвращаем исходный IP
        yield Host(ip=str(start_ip), origin=addr, host_type=HostType.IP)
        
        # БЕСКОНЕЧНЫЙ РЕЖИМ для одиночного IP
        low_ip = start_ip
        high_ip = start_ip
        

        print(f"[+] Enable infinite mode, init: {start_ip}")
        debug_log(f"GENERATOR: Infinite mode enabled from {start_ip}")
        
        i = 0
        debug_log(f"GENERATOR: Entering infinite loop")

        while True:
            debug_log(f"GENERATOR: Loop iteration {i}, low_ip={low_ip}, high_ip={high_ip}")
            if i % 2 == 0:
                # Четная итерация: уменьшаем low_ip
                low_ip = low_ip - 1
                if low_ip >= ipaddress.ip_address('0.0.0.0'):
                    debug_log(f"GENERATOR: Yielding low_ip: {low_ip}")
                    yield Host(ip=str(low_ip), origin=str(low_ip), host_type=HostType.IP)
                else:
                    debug_log(f"GENERATOR: low_ip out of bounds, skipping")
            else:
                # Нечетная итерация: увеличиваем high_ip
                high_ip = high_ip + 1
                if high_ip <= ipaddress.ip_address('255.255.255.255'):
                    yield Host(ip=str(high_ip), origin=str(high_ip), host_type=HostType.IP)
                else:
                    debug_log(f"GENERATOR: low_ip out of bounds, skipping")
            i += 1
            time.sleep(0.1)  # КРИТИЧЕСКИ ВАЖНО: задержка 100ms

            
    except ValueError:
        # Не одиночный IP, пробуем как CIDR
        try:
            network = ipaddress.ip_network(addr, strict=False)
            
            # Проверяем IPv6
            if isinstance(network, ipaddress.IPv6Network) and not enable_ipv6:
                return
            
            print(f"[*] Parsing CIDR: {network}")
            
            # Для CIDR/32 (одиночный IP) тоже включаем бесконечный режим
            if network.prefixlen == 32 and network.version == 4:
                print(f"[+] CIDR/32 detected, enabling infinite mode")
                start_ip = list(network.hosts())[0]
                
                yield Host(ip=str(start_ip), origin=addr, host_type=HostType.CIDR)
                
                # БЕСКОНЕЧНЫЙ РЕЖИМ
                low_ip = start_ip
                high_ip = start_ip
                
                i = 0
                while True:
                    if i % 2 == 0:
                        low_ip = low_ip - 1
                        if low_ip >= ipaddress.ip_address('0.0.0.0'):
                            yield Host(ip=str(low_ip), origin=str(low_ip), host_type=HostType.CIDR)
                    else:
                        high_ip = high_ip + 1
                        if high_ip <= ipaddress.ip_address('255.255.255.255'):
                            yield Host(ip=str(high_ip), origin=str(high_ip), host_type=HostType.CIDR)
                    
                    i += 1
            else:
                # Обычный CIDR (не /32) - просто перебираем хосты
                for host in network.hosts():
                    yield Host(ip=str(host), origin=addr, host_type=HostType.CIDR)
            
        except ValueError:
            # Не IP и не CIDR, пробуем как домен
            try:
                print(f"[*] Trying to resolve as domain: {addr}")
                ips = socket.getaddrinfo(addr, None)
                for family, _, _, _, sockaddr in ips:
                    ip_str = sockaddr[0]
                    if family == socket.AF_INET or (enable_ipv6 and family == socket.AF_INET6):
                        start_ip = ipaddress.ip_address(ip_str)
                        
                        print(f"[+] Domain resolved to {start_ip}, enabling infinite mode")
                        
                        yield Host(ip=ip_str, origin=addr, host_type=HostType.DOMAIN)
                        
                        # БЕСКОНЕЧНЫЙ РЕЖИМ для домена
                        low_ip = start_ip
                        high_ip = start_ip
                        
                        i = 0
                        while True:
                            if i % 2 == 0:
                                low_ip = low_ip - 1
                                if low_ip >= ipaddress.ip_address('0.0.0.0'):
                                    yield Host(ip=str(low_ip), origin=str(low_ip), host_type=HostType.DOMAIN)
                            else:
                                high_ip = high_ip + 1
                                if high_ip <= ipaddress.ip_address('255.255.255.255'):
                                    yield Host(ip=str(high_ip), origin=str(high_ip), host_type=HostType.DOMAIN)
                            
                            i += 1
                        break
                        
            except Exception as e:
                print(f"[-] Cannot parse {addr}: {e}")
                return

def iterate_file(filepath: str, enable_ipv6=False) -> Generator[Host, None, None]:
    """Чтение хостов из файла (без бесконечного режима)"""
    with open(filepath, 'r') as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            
            # Пробуем как CIDR
            try:
                network = ipaddress.ip_network(line, strict=False)
                if isinstance(network, ipaddress.IPv4Network) or (enable_ipv6 and isinstance(network, ipaddress.IPv6Network)):
                    for host in network.hosts():
                        yield Host(ip=str(host), origin=line, host_type=HostType.CIDR)
                    continue
            except ValueError:
                pass
            
            # Пробуем как IP адрес
            try:
                ip = ipaddress.ip_address(line)
                if isinstance(ip, ipaddress.IPv4Address) or (enable_ipv6 and isinstance(ip, ipaddress.IPv6Address)):
                    yield Host(ip=str(ip), origin=line, host_type=HostType.IP)
                    continue
            except ValueError:
                pass
            
            # Считаем доменом
            if '.' in line:
                yield Host(ip=None, origin=line, host_type=HostType.DOMAIN)

def fetch_domains_from_url(url: str) -> List[str]:
    """Извлечение доменов из веб-страницы"""
    import aiohttp
    import asyncio
    
    async def _fetch():
        async with aiohttp.ClientSession() as session:
            async with session.get(url, ssl=False) as resp:
                text = await resp.text()
                pattern = r'https?://([A-Za-z0-9\-\.]+)[/\"\'\s<>]'
                domains = re.findall(pattern, text)
                return list(set(domains))
    
    return asyncio.run(_fetch())