import numpy as np
from sklearn import svm
from statsmodels.tsa.arima.model import ARIMA
from thesis.layers.predictor.predictors.i_predictor import IPredictor
import tensorflow as tf


class SVRPredictor(IPredictor):
    def __init__(self, config, feature_data_list):
        super().__init__("SVR", config, feature_data_list)


    def make_prediction(self, config, train, test):

        model = svm.SVR(kernel=config["hyperParameters"]["svr-kernel"])
        model.fit(np.array(train["timestamp"].values).reshape(-1, 1), train["data"].values)

        result = model.predict(np.array(test["timestamp"].values).reshape(-1, 1))
        prediction = test.copy()

        prediction["data"] = result
        return prediction



