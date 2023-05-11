import pickle

import numpy as np
from statsmodels.tsa.arima.model import ARIMA
from thesis.layers.predictor.predictors.i_predictor import IPredictor
import tensorflow as tf


class NeuralProphetPredictor(IPredictor):
    def __init__(self, config, feature_data_list):
        super().__init__("NEURAL_PROPHET", config, feature_data_list)


    def make_prediction(self, config, train, test):
        with open('neuralprophet_model.pkl', 'rb') as f:
            m = pickle.load(f)
        m.restore_trainer()
        forecast = m.predict(tet)
        """model = ARIMA(
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
        prediction["data"] = result"""

        forecast['yhat1'].values

        return None #prediction
