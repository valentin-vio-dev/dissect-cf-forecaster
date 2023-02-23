import socket
import json
from predictor import Predictor
from logger import Logger
import sys


def start_socket(config):
    if config["host"] is None or config["port"] is None:
        raise Exception("Host or port is not provided!")

    predictor = Predictor(config)
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((config["host"], int(config["port"])))
        s.listen()
        Logger.log(f"Waiting for connection on {config['host']}:{config['port']}")

        conn, addr = s.accept()
        with conn:
            Logger.log(f"Connection from {addr}")

            while True:
                data = conn.recv(1024 * 100)
                if not data:
                    Logger.log("Connection closed!")
                    break

                data = json.loads(data).copy()

                print(sys.getsizeof(data))
                print(data)
                Logger.log(f"Data keys: {list(data.keys())}")

                result = predictor.compute(data)

                Logger.log(f"Response: {str(result)[0:128]}...")
                Logger.log(f"Response keys: {list(result.keys())}")
                Logger.log("---")

                response = (json.dumps(result) + "\r\n").encode("ascii")
                conn.sendall(response)
