import json

import webview
import atexit

from socket_bridge import SocketBridge
from thesis.layers.predictor.App import App


"""
TODO:
TODO
TODO
- Send back data to Application layer
- Send command from Application layer to stop UI when simulation is over
- Predictor layer messages refactor
- UI messages refactor
- Show current prediction number on UI
- Refactor Predictor loggers
- Close Application layer window
- LTSM inverscale
----------------------
- *Input error handlig
- *Ui status messages
- *Predictor try catch show errors

"""


if __name__ == "__main__":
    app = App()
    socket = SocketBridge()

    app.set_socket(socket)
    socket.set_app(app)

    atexit.register(app.close_application_layer)

    window = webview.create_window("DCF", "interface/web/index.html", width=1450, height=800, js_api=app)
    webview.start()

