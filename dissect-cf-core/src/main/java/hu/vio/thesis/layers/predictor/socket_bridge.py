import os
import socket
import json


from logger import Logger
import traceback

from thesis.layers.predictor.message_protocol import create_message
from thesis.layers.predictor.types import AppType
from thesis.layers.predictor.utils import Utils


class SocketBridge:
    def __init__(self):
        self.app = None

    def set_app(self, app):
        self.app = app

    def start_socket(self, config):
        self.app.add_message(
            AppType.PREDICTOR,
            "LOG",
            "socket",
            f"Set config {str(config)}"
        )
        
        if config["host"] is None or config["port"] is None:
            raise Exception("Host or port is not provided!")

        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.bind((config["host"], int(config["port"])))
            s.listen()
            Logger.log(f"Waiting for connection on {config['host']}:{config['port']}")
            self.app.add_message(
                AppType.PREDICTOR,
                "LOG",
                "socket",
                f"Waiting for connection on {config['host']}:{config['port']}"
            )
            self.start_application_layer(config)
            conn, addr = s.accept()

            with conn:
                Logger.log(f"Connection from {addr}")
                self.app.add_message(
                    AppType.PREDICTOR,
                    "LOG",
                    "socket",
                    f"Connection from {addr}"
                )
                data = b''
                message_size = None

                while True:
                    packet = conn.recv(1024)

                    if not packet: # Is it needed?
                        Logger.log("Connection closed!")
                        break

                    try:
                        if "socket-message-size" in str(packet):
                            json_data = json.loads(packet).copy()
                            message_size = int(json_data["message"])
                            conn.sendall(create_message("OTHER", "socket-message-size", "OK"))
                        else:
                            data += packet
                            if len(data) >= message_size:
                                json_data = json.loads(data).copy()
                                Logger.log(Utils.trim_str(str(json_data), 200))

                                if json_data["command"] == "LOG":
                                    pass
                                elif json_data["command"] == "DATA":
                                    self.app.predict(json_data)
                                    print("-------------")
                                    print(json_data)
                                    print("-------------")
                                    pass
                                elif json_data["command"] == "COMMAND":
                                    pass
                                elif json_data["command"] == "OTHER":
                                    pass

                                conn.sendall(create_message("DATA", "response", "TODO"))

                                data = b''
                                message_size = None

                    except Exception as e:
                        Logger.log(f"Error: {str(e)}")
                        traceback.print_exc()
                        conn.sendall(b'ERROR\r\n')

    def start_application_layer(self, args):
        """
        At kell irni, ha mas is hasznalni akarja.
        Ez az utvonal a DissectCFbol lett kimasolva.
        Csak ugy mukodik ha elotte le van buildelve.
        """
        start_string = r'start /min "DCFApplicationLayer" cmd /k; C:\Users\valen\.jdks\openjdk-19.0.1\bin\java.exe "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2022.2.3\lib\idea_rt.jar=60411:C:\Program Files\JetBrains\IntelliJ IDEA 2022.2.3\bin" -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath D:\dev\dissect-cf\dissect-cf-core\target\classes;C:\Users\valen\.m2\repository\net\sf\trove4j\trove4j\3.0.3\trove4j-3.0.3.jar;C:\Users\valen\.m2\repository\org\xerial\sqlite-jdbc\3.8.7\sqlite-jdbc-3.8.7.jar;C:\Users\valen\.m2\repository\org\apache\commons\commons-lang3\3.8.1\commons-lang3-3.8.1.jar;C:\Users\valen\.m2\repository\javax\xml\bind\jaxb-api\2.1\jaxb-api-2.1.jar;C:\Users\valen\.m2\repository\javax\xml\stream\stax-api\1.0-2\stax-api-1.0-2.jar;C:\Users\valen\.m2\repository\javax\activation\activation\1.1\activation-1.1.jar;C:\Users\valen\.m2\repository\com\sun\xml\bind\jaxb-impl\2.3.2\jaxb-impl-2.3.2.jar;C:\Users\valen\.m2\repository\org\json\json\20180130\json-20180130.jar;C:\Users\valen\.m2\repository\com\sun\xml\bind\jaxb-core\2.3.0\jaxb-core-2.3.0.jar hu.vio.thesis.layers.application.MainScenario '
        args_array = []
        for key in args:
            args_array.append(f"{key}=\"{args[key]}\"")

        print(" ".join(args_array))
        os.system(start_string + " ".join(args_array))

    def stop_application_layer(self):
        os.system(r'taskkill /FI "WindowTitle eq DCFApplicationLayer *"')
