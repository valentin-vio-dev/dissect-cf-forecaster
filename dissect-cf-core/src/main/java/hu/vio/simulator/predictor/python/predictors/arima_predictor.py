import matplotlib.pyplot as plt
from statsmodels.tsa.arima.model import ARIMA
from utils import Utils


class ArimaPredictor:
    def __init__(self, train, test, original):
        self._train = train
        self._test = test
        self._original = original

    def predict(self):
        model = ARIMA(self._train["data"].values, order=(2, 0, 0))
        fitted = model.fit()
        result = fitted.forecast(len(self._test.index), alpha=0.05)
        prediction = self._test.copy()
        prediction["data"] = result

        plt.plot(self._original["data"], color="lightgray", label="Original")
        plt.plot(self._train["data"], color="b", label="Train")
        plt.plot(self._test["data"], color="r", label="Test")
        plt.plot(prediction["data"], color="g", label="Prediction", linestyle="dashed")
        plt.axvline(x=len(self._train["data"]), color="black")

        plt.legend(loc="upper left")
        plt.grid()

        plt.savefig(Utils.create_and_get_output_directory("arima") + "/" + Utils.get_current_date())

        plt.clf()

        return prediction["data"].to_list()
