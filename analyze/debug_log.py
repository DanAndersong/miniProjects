import time

# Глобальная переменная
_DEBUG_ENABLED = False

def set_debug(enabled: bool):
    """Включить или выключить дебаг-логирование"""
    global _DEBUG_ENABLED
    _DEBUG_ENABLED = enabled
    if enabled:
        print(f"[*] Debug logging enabled")

def debug_log(msg: str):
    """Функция для отладочного вывода"""
    if _DEBUG_ENABLED:
        timestamp = time.strftime('%H:%M:%S')
        print(f"[DEBUG {timestamp}] {msg}")

def is_debug_enabled() -> bool:
    """Проверить, включен ли дебаг"""
    return _DEBUG_ENABLED
