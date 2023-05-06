import numpy as np
from statsmodels.tsa.arima.model import ARIMA
from thesis.layers.predictor.predictors.i_predictor import IPredictor
import tensorflow as tf


class ArimaPredictor(IPredictor):
    def __init__(self, config, feature_data_list):
        super().__init__("ARIMA", config, feature_data_list)

    def prepare_data(self, data, windows_size):
        X, y = [], []
        for i in range(len(data) - windows_size):
            X.append(data[i:i + windows_size])
            y.append(data[i + windows_size])
        return np.array(X), np.array(y)

    def make_prediction(self, config, train, test):
        model = ARIMA(
            train["data"].values,
            order=(
                config["hyperParameters"]["arima-p_value"],
                config["hyperParameters"]["arima-d_value"],
                config["hyperParameters"]["arima-q_value"]
            )
        )
        fitted = model.fit()
        result = fitted.forecast(
            len(test.index),
            alpha=config["hyperParameters"]["arima-alpha"]
        )
        prediction = test.copy()
        prediction["data"] = result

        return prediction
