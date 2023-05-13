import pandas as pd

from skforecast.ForecasterAutoreg import ForecasterAutoreg
from sklearn.ensemble import RandomForestRegressor

from thesis.layers.predictor.predictors.i_predictor import IPredictor


class RandomForestPredictor(IPredictor):
    def __init__(self, config, feature_data_list):
        super().__init__("RANDOM_FOREST", config, feature_data_list)

    def make_prediction(self, config, train, test):
        model = ForecasterAutoreg(
            regressor=RandomForestRegressor(
                n_estimators=config["hyperParameters"]["random_forest-number_of_trees"],
                max_depth=config["hyperParameters"]["random_forest-max_depth"] if config["hyperParameters"]["random_forest-max_depth"] >= 1 else None
            ),
            lags=config["hyperParameters"]["random_forest-lags"]
        )
        model.fit(y=train["data"])
        predictions = model.predict(steps=len(test))

        prediction = test.copy()
        prediction["data"] = predictions.values

        return prediction
