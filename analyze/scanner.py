import ssl
import socket
import time

# Импортируем дебаг из общего модуля
from debug_log import debug_log, set_debug  # не debug_utils!

from dataclasses import dataclass
from typing import Optional
from enum import Enum


class TLSVersion(Enum):
    TLS1_0 = "TLSv1.0"
    TLS1_1 = "TLSv1.1"
    TLS1_2 = "TLSv1.2"
    TLS1_3 = "TLSv1.3"
    UNKNOWN = "UNKNOWN"

@dataclass
class TLSInfo:
    ip: str
    origin: str
    tls_version: TLSVersion
    alpn_protocol: Optional[str]
    cert_domain: str
    cert_issuer: str
    feasible: bool = False

def scan_tls(host_ip: str, host_origin: str, port: int = 443, 
             timeout: int = 10, server_name: Optional[str] = None) -> Optional[TLSInfo]:
    """Сканирование TLS параметров (точная копия Go логики)"""
    debug_log(f"SCAN: Starting scan for {host_ip}:{port}")  
    if not host_ip:
        return None
    
    hostport = f"{host_ip}:{port}"
    
    try:
        # Создаем TCP соединение
        sock = socket.create_connection((host_ip, port), timeout=timeout)
        sock.settimeout(timeout)
        debug_log(f"SCAN: Connection successful to {host_ip}")

        # Создаем SSL контекст
        context = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
        context.check_hostname = False
        context.verify_mode = ssl.CERT_NONE
        
        # Настраиваем ALPN как в оригинале
        try:
            context.set_alpn_protocols(['h2', 'http/1.1'])
        except:
            pass  # ALPN не поддерживается    
        # Если сканируем домен, устанавливаем Server Name
        if server_name:
            ssl_sock = context.wrap_socket(sock, server_hostname=server_name)
        else:
            ssl_sock = context.wrap_socket(sock, server_hostname=host_ip)
        
        # Получаем информацию о соединении
        version_str = ssl_sock.version()
        alpn = ssl_sock.selected_alpn_protocol() if hasattr(ssl_sock, 'selected_alpn_protocol') else None
        
        cert = ssl_sock.getpeercert()
        cert_domain = ""
        cert_issuer = ""
        
        if cert:
            # 1. Извлекаем CommonName (домен)
            # cert возвращает список кортежей, а не словарь как раньше
            for field in cert.get('subject', []):
                # field это кортеж кортежей: ((key, value), (key, value), ...)
                for item in field:
                    if item[0] == 'commonName':
                        cert_domain = item[1]
                        break
            
            # 2. Извлекаем Issuer
            issuer_parts = []
            for field in cert.get('issuer', []):
                for item in field:
                    if item[0] == 'organizationName':
                        issuer_parts.append(item[1])
                    elif item[0] == 'commonName' and not issuer_parts:
                        # fallback: используем CN если нет organizationName
                        issuer_parts.append(item[1])
            
            cert_issuer = " | ".join(issuer_parts) if issuer_parts else ""
        
        # Альтернативный способ через binary cert
        if not cert_domain:
            try:
                # Получаем сертификат в бинарном формате
                der_cert = ssl_sock.getpeercert(binary_form=True)
                if der_cert:
                    import OpenSSL
                    x509 = OpenSSL.crypto.load_certificate(OpenSSL.crypto.FILETYPE_ASN1, der_cert)
                    cert_domain = x509.get_subject().CN or ""
                    cert_issuer = x509.get_issuer().CN or ""
            except:
                pass        
        
        # Определяем версию TLS
        version_map = {
            'TLSv1': TLSVersion.TLS1_0,
            'TLSv1.1': TLSVersion.TLS1_1,
            'TLSv1.2': TLSVersion.TLS1_2,
            'TLSv1.3': TLSVersion.TLS1_3,
        }
        tls_version = version_map.get(version_str, TLSVersion.UNKNOWN)
        
        debug_log(f"SCAN: Results - {host_ip}: version={version_str}, "
                  f"alpn={alpn}, domain='{cert_domain}', issuer='{cert_issuer}'")
        
        # Критерии feasible как в оригинале: TLS 1.3 + HTTP/2 + валидный сертификат
        feasible = (tls_version == TLSVersion.TLS1_3 and 
                   alpn == 'h2' and 
                   cert_domain and 
                   cert_issuer)

        debug_log(f"SCAN: Feasible check - TLS1.3={tls_version == TLSVersion.TLS1_3}, "
                  f"h2={alpn == 'h2'}, has_domain={bool(cert_domain)}, "
                  f"has_issuer={bool(cert_issuer)} => {feasible}")

        if feasible:
            debug_log(f"SCAN: ✓ FEASIBLE SITE FOUND: {host_ip}")
        
        ssl_sock.close()
        sock.close()
        
        return TLSInfo(
            ip=host_ip,
            origin=host_origin,
            tls_version=tls_version,
            alpn_protocol=alpn,
            cert_domain=cert_domain,
            cert_issuer=cert_issuer,
            feasible=feasible
        )
        
    except socket.timeout:
        return None
    except ConnectionRefusedError:
        return None
    except ssl.SSLError as e:
        return None
    except Exception as e:
        return None