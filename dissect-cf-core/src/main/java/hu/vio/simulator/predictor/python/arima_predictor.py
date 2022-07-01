import matplotlib.pyplot as plt
from statsmodels.tsa.arima.model import ARIMA
import os
from datetime import datetime

ROOT_DIR = os.path.dirname(os.path.abspath(__file__))

class ArimaPredictor:
    def __init__(self, train, test):
        self._train = train
        self._test = test
        pass

    def predict(self):
        model = ARIMA(self._train["data"].values, order = (2, 0, 0))
        fitted = model.fit()
        res = fitted.forecast(len(self._test.index), alpha = 0.05)
        data_pred = self._test.copy()
        data_pred["data"] = res

        plt.axvline(x = len(self._train["data"]), color = "black")

        plt.plot(self._train["data"], color = "b", label = "Train")
        plt.plot(self._test["data"], color = "r", label = "Test")
        plt.plot(data_pred["data"], color = "g", label = "Prediction", linestyle = "dashed")

        plt.legend(loc = "upper left")
        plt.grid()

        plt.savefig(ROOT_DIR + "/images/" + datetime.now().strftime("%Y_%m_%d_%H_%M_%S"))

        return data_pred["data"].values