import matplotlib.pyplot as plt
import pandas as pd
from utils import Utils
from prophet import Prophet


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

        model = Prophet()
        model.fit(dataframe_processed)
        future_dates = model.make_future_dataframe(periods=len(self._test.index))
        result = model.predict(future_dates)

        prediction = self._test.copy()
        prediction["data"] = result["yhat"].values[len(dataframe_processed.index):]

        self.save_plot(prediction)
        return prediction["data"].to_list()

    def save_plot(self, prediction):
        plt.plot(self._original["data"], color="lightgray", label="Original")
        plt.plot(self._train["data"], color="b", label="Train")
        plt.plot(self._test["data"], color="r", label="Test")
        plt.plot(prediction["data"], color="g", label="Prediction", linestyle="dashed")
        plt.axvline(x=len(self._train["data"]), color="black")

        plt.legend(loc="upper left")
        plt.grid()

        plt.savefig(Utils.create_and_get_output_directory("prophet") + "/" + Utils.get_current_date())

        plt.clf()

    def name(self):
        return "PROPHET"
