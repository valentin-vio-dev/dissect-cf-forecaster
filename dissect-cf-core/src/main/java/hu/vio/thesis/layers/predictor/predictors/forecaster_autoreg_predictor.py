import pandas as pd
from statsmodels.tsa.holtwinters import SimpleExpSmoothing, Holt, ExponentialSmoothing
import plotly.graph_objects as go
from scipy.signal import lfilter

from skforecast.ForecasterAutoreg import ForecasterAutoreg
from sklearn.ensemble import RandomForestRegressor

import matplotlib.pyplot as plt



from thesis.layers.predictor.utils import Utils

dataframe = pd.read_csv(
    "D:\dev\dissect-cf\dissect-cf-core\src\main\java\hu/vio/thesis/layers\predictor/dataset/CA_Kecskemet_Airport.csv",
    sep=";")
dataframe = dataframe[600:600 + 256]


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
n = 10
b = [1.0 / n] * n
a = 1
dataframe["LOAD_OF_RESOURCE"] = lfilter(b, a, dataframe["LOAD_OF_RESOURCE"].values)
dataframe = dataframe.drop(columns=['MEMORY', 'TOTAL_PROC_POWER'])

dataframe = dataframe.rename(columns={'LOAD_OF_RESOURCE': 'y'})

print(dataframe)
train, test = Utils.train_test_split(dataframe)

forecaster = ForecasterAutoreg(regressor=RandomForestRegressor(random_state=123), lags=32)
forecaster.fit(y=train['y'])
predictions = forecaster.predict(steps=len(test))

fig, ax = plt.subplots(figsize=(9, 4))
train['y'].plot(ax=ax, label='train')
test['y'].plot(ax=ax, label='test')
predictions.plot(ax=ax, label='predictions')
ax.legend()
fig.show()