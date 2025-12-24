import geoip2.database
import threading
from pathlib import Path
from debug_log import debug_log, set_debug  # не debug_utils!

class Geo:
    def __init__(self, mmdb_path="Country.mmdb"):
        debug_log(f"GEO: Initializing with {mmdb_path}")

        self.reader = None
        self.mutex = threading.Lock()
        
        if Path(mmdb_path).exists():
            try:
                self.reader = geoip2.database.Reader(mmdb_path)
                print(f"[+] Enabled GeoIP")
            except Exception as e:
                print(f"[-] Cannot open {mmdb_path}: {e}")
        else:
            print(f"[-] GeoIP database not found: {mmdb_path}")
    
    def get_country(self, ip_str):
        if not self.reader:
            return "N/A"
        
        try:
            with self.mutex:
                response = self.reader.country(ip_str)
                return response.country.iso_code or "N/A"
        except Exception:
            return "N/A"
    
    def close(self):
        if self.reader:
            self.reader.close()