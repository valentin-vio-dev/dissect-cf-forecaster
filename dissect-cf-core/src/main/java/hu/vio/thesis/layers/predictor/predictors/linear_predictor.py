import numpy as np
from sklearn.linear_model import LinearRegression

from thesis.layers.predictor.predictors.i_predictor import IPredictor


class LinearPredictor(IPredictor):
    def __init__(self, config, feature_data_list):
        super().__init__("LINEAR_REGRESSION", config, feature_data_list)

    def make_prediction(self, config, train, test):
        model = LinearRegression()
        model.fit(np.array(train["timestamp"].values).reshape(-1, 1), train["data"].values)

        result = model.predict(np.array(test["timestamp"].values).reshape(-1, 1))
        prediction = test.copy()

        prediction["data"] = result
        return prediction
