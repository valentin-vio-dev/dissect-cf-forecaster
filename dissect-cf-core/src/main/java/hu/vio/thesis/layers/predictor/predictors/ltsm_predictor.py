import json

import numpy as np
from statsmodels.tsa.arima.model import ARIMA
from thesis.layers.predictor.predictors.i_predictor import IPredictor
import tensorflow as tf

from thesis.layers.predictor.utils import Utils


class LTSMPredictor(IPredictor):
    def __init__(self, config, feature_data_list):
        super().__init__("LTSM", config, feature_data_list)

    def prepare_data(self, data, windows_size):
        X, y = [], []
        for i in range(len(data) - windows_size):
            X.append(data[i:i + windows_size])
            y.append(data[i + windows_size])
        return np.array(X), np.array(y)

    def make_prediction(self, config, train, test):
        model_config = self.get_model_config(config)

        model = tf.keras.models.load_model(config['hyperParameters']['ltsm-model_location'])

        joined = [*train["data"].values, *test["data"].values]
        X_test, y_test = self.prepare_data(joined, model_config["windowSize"])
        predicted = model.predict(X_test)
        predicted = predicted.flatten()

        prediction = test.copy()
        prediction["data"] = predicted[-64:]

        return prediction

    def get_model_config(self, config):
        print(config)
        path = Utils.replace_all(config['hyperParameters']['ltsm-model_location'], "\\", "/")
        path = path.split("/")[0:-1]
        path = "/".join(path)
        with open(path + "/model_config.json", "r") as f:
            model_config = json.load(f)
        return model_config

