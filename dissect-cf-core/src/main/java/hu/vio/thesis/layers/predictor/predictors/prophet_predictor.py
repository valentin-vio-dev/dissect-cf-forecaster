import matplotlib.pyplot as plt
import pandas as pd
from thesis.layers.predictor.utils import Utils
#from fbprophet import Prophet


class ProphetPredictor:
    def __init__(self, train, test, original):
        self._train = train
        self._test = test
        self._original = original

    def predict(self):
        dataframe_processed = self._train.copy()
        dataframe_processed.rename({"data": "y", "timestamp": "ds"}, axis=1, inplace=True)
        dataframe_processed.reset_index(inplace=True)

        dates = []
        start_date = "01/01/1900"
        for i in range(0, len(dataframe_processed.index)):
            end_date = pd.to_datetime(start_date) + pd.DateOffset(days=i)
            end_date = str(end_date).split(" ")[0]
            dates.append(end_date)
        dataframe_processed["ds"] = dates

        model = {}#Prophet()
        model.fit(dataframe_processed)
        future_dates = model.make_future_dataframe(periods=len(self._test.index))
        result = model.predict(future_dates)

        prediction = self._test.copy()
        prediction["data"] = result["yhat"].values[len(dataframe_processed.index):]

        self.save_plot(prediction)
        return prediction["data"].to_list()



from thesis.layers.predictor.predictors.i_predictor import IPredictor
from fbprophet import Prophet

class ProphetPredictor(IPredictor):
    def __init__(self, config, feature_data_list):
        super().__init__("ARIMA", config, feature_data_list)

    def make_prediction(self, config, train, test):
        model = Prophet()
        model.fit(train)
        future = model.make_future_dataframe(periods=365)
        model.predict(future)

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
