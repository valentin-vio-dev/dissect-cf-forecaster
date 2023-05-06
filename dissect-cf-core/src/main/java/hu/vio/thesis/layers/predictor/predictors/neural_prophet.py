import pandas as pd
from statsmodels.tsa.holtwinters import SimpleExpSmoothing, Holt, ExponentialSmoothing
import plotly.graph_objects as go
from scipy.signal import lfilter
import matplotlib.pyplot as plt

from thesis.layers.predictor.preprocessor import Preprocessor
from thesis.layers.predictor.utils import Utils


from neuralprophet import NeuralProphet



dataframe = pd.read_csv("D:\dev\dissect-cf\dissect-cf-core\src\main\java\hu/vio/thesis/layers\predictor/dataset/CA_Kecskemet_Airport.csv", sep=";")
dataframe = dataframe[200:200+256]

def replace_commas(cell):
    return str(cell).replace(",", ".")


dates = []
start_date = "01/01/1900"
for i in range(0, len(dataframe.index)):
    end_date = pd.to_datetime(start_date) + pd.DateOffset(days=i)
    end_date = str(end_date).split(" ")[0]
    dates.append(end_date)
dataframe["ds"] = dates

dataframe["LOAD_OF_RESOURCE"] = dataframe["LOAD_OF_RESOURCE"].apply(replace_commas).astype(float).tolist()
dataframe["LOAD_OF_RESOURCE"] = Preprocessor.smooth_data(dataframe["LOAD_OF_RESOURCE"].values, 20)
dataframe = dataframe.drop(columns=['MEMORY', 'TOTAL_PROC_POWER'])

dataframe = dataframe.rename(columns={'LOAD_OF_RESOURCE': 'y'})


print(dataframe)
train, test = Utils.train_test_split(dataframe)

"""m = NeuralProphet().fit(train, freq="D")
df_future = m.make_future_dataframe(train, periods=64)
forecast = m.predict(df_future)

print(forecast)

plt.plot(forecast.ds, forecast.y)
plt.plot(dataframe["ds"], dataframe["y"])
plt.show()"""

m = NeuralProphet()
m.fit(train, freq="D")
df_future = m.make_future_dataframe(train, periods=30)
forecast = m.predict(df_future)
fig_forecast = m.plot(forecast)


"""from thesis.layers.predictor.predictors.i_predictor import IPredictor


class NeuralProphetPredictor(IPredictor):
    def __init__(self, config, feature_data_list):
        super().__init__("NEURAL_PROPHET", config, feature_data_list)

    def make_prediction(self, config, train, test):
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

"""
