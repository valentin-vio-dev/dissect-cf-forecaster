import socket
import json
from predictor import Predictor


HOST = "127.0.0.1"
PORT = 65432
predictor = Predictor()


with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    
    print(f"Socket listening on {HOST}:{PORT}...")

    conn, addr = s.accept()
    with conn:
        print(f"Connected by {addr}")
        while True:
            data = conn.recv(1024 * 100)
            if not data:
                break
            
            print(str(data.decode())[0:128] + "...")
            result = predictor.compute(json.loads(data).copy())
            response = (json.dumps(result) + "\r\n").encode("ascii")
      
            conn.sendall(response)
    