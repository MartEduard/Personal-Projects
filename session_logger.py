import json
import time

class SessionLogger:
    def __init__(self, filename="session_log.json"):
        self.filename=filename

    def log_session(self, username, action, details=None):
        session_entry = {
            "username": username,
            "action": action,
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()),
            "details": details or {}
        }
        try:
            with open(self.filename, "a") as file: # Append mode
                file.write(json.dumps(session_entry) + "\n")

        except IOError as e:
            print(f"Error writing to log file: {e}")