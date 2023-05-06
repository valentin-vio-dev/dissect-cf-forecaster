import pandas as pd

from skforecast.ForecasterAutoreg import ForecasterAutoreg
from sklearn.ensemble import RandomForestRegressor

from thesis.layers.predictor.predictors.i_predictor import IPredictor


class ForecasterAutoregPredictor(IPredictor):
    def __init__(self, config, feature_data_list):
        super().__init__("FORECASTER_AUTOREG", config, feature_data_list)

    def make_prediction(self, config, train, test):
        """dates = []
        start_date = "01/01/1900"
        for i in range(0, len(train.index)):
            end_date = pd.to_datetime(start_date) + pd.DateOffset(days=i)
            end_date = str(end_date).split(" ")[0]
            dates.append(end_date)
        train["ds"] = dates"""
        train = train.rename(columns={"data": "y", "timestamp": "ds"})
        """train = train.drop("timestamp", axis=1)"""

        forecaster = ForecasterAutoreg(
            regressor=RandomForestRegressor(
                random_state=config["hyperParameters"]["forecaster_autoreg-random_state"]
            ),
            lags=config["hyperParameters"]["forecaster_autoreg-lags"]
        )
        forecaster.fit(y=train["y"])
        predictions = forecaster.predict(steps=len(test))

        prediction = test.copy()
        prediction["data"] = predictions.values

        return prediction
