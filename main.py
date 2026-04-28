from server import ServerMQTT
from user_interface import GUI

def main():
    ui = GUI(None)  # Create the UI instance without the server instance initially
    server_instance = ServerMQTT(ui)  # Create the server instance with the UI reference
    ui.server_instance = server_instance  # Set the server instance in the UI
    ui.mainloop()

if __name__ == "__main__":
    main()