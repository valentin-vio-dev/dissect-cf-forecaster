import sys
from local_socket import start_socket
from args_processor import process_args

if __name__ == "__main__":
    args = sys.argv[1:]
    config = process_args(args)  # --host=127.0.0.1 --port=65432
    start_socket(config)
