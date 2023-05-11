import pandas as pd

from skforecast.ForecasterAutoreg import ForecasterAutoreg
from sklearn.ensemble import RandomForestRegressor

from thesis.layers.predictor.predictors.i_predictor import IPredictor


class ForecasterAutoregPredictor(IPredictor):
    def __init__(self, config, feature_data_list):
        super().__init__("FORECASTER_AUTOREG", config, feature_data_list)

    def make_prediction(self, config, train, test):
        forecaster = ForecasterAutoreg(
            regressor=RandomForestRegressor(
                n_estimators=config["hyperParameters"]["random_forest-n_estimators"],
                max_depth=config["hyperParameters"]["random_forest-max_depth"]
            ),
            lags=config["hyperParameters"]["random_forest-lags"]
        )
        forecaster.fit(y=train["y"])
        predictions = forecaster.predict(steps=len(test))

        prediction = test.copy()
        prediction["data"] = predictions.values

        return prediction
