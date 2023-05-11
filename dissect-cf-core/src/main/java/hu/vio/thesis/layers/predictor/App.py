import os

import webview

from thesis.layers.predictor.logger import Logger
from thesis.layers.predictor.predictor import Predictor
from thesis.layers.predictor.predictors.ltsm_trainer import LTSMTrainer
from thesis.layers.predictor.types import AppType
from thesis.layers.predictor.utils import Utils


class App:

    def __init__(self):
        self.socket_bridge = None
        self.messages = []
        self.messages_up = []
        self.messages_down = []
        self.predictor = None

    def set_socket(self, socket_bridge):
        self.socket_bridge = socket_bridge

    def set_config(self, config):   ## Atirni start_application_layer() mert ez van a diplomamnukaban
        Utils.create_repostory(config["outputLocation"])
        self.predictor = Predictor(self, config)
        self.socket_bridge.start_socket(config)

    def close_application_layer(self):
        self.socket_bridge.stop_application_layer()

    def predict(self, data):
        prediction = self.predictor.compute(data)
        self.add_message(AppType.PREDICTOR, "IMAGE", "prediction-image", prediction["image"])
        self.add_message(AppType.PREDICTOR, "DATA", "prediction-avg-rmse", prediction["avg_rmse"])
        self.add_message(AppType.PREDICTOR, "DATA", "prediction-avg-mae", prediction["avg_mae"])
        self.add_message(AppType.PREDICTOR, "DATA", "prediction-avg-mse", prediction["avg_mse"])

    def add_message(self, application_type, command, event, message):
        m = {
            "layer": application_type.value,
            "command": command,
            "event": event,
            "message": message
        }
        self.messages.append(m)
        Logger.log(Utils.trim_str(str(message), 200))

    def check_model(self, path):
        if os.path.isfile(path):
            return os.path.exists(path) and ".h5" in path
        return False

    def set_training_model_config(self, config):
        print(config)
        ltsm_trainer = LTSMTrainer(config)
        ltsm_trainer.load_dataset()
        ltsm_trainer.prepare_data()
        ltsm_trainer.set_model()
        ltsm_trainer.start_training()
        ltsm_trainer.save_model()
        self.add_message(AppType.PREDICTOR, "COMMAND", "model-training-status", "completed")

    def get_message(self):
        return self.messages.pop()
