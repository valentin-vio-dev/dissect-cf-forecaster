import pandas as pd
from statsmodels.tsa.holtwinters import SimpleExpSmoothing, Holt, ExponentialSmoothing
import plotly.graph_objects as go
from scipy.signal import lfilter
import matplotlib.pyplot as plt
from sklearn import svm
from sklearn.tree import DecisionTreeRegressor
import numpy as np

from thesis.layers.predictor.preprocessor import Preprocessor
from thesis.layers.predictor.utils import Utils

import random


from neuralprophet import NeuralProphet



dataframe = pd.read_csv("D:\dev\dissect-cf\dissect-cf-core\src\main\java\hu/vio/thesis/layers\predictor/dataset/CA_Kecskemet_Airport.csv", sep=";")
dataframe = dataframe[2000:2000+256]

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



y = dataframe["y"].values
x = [i for i in range(0, len(y))]

df = pd.DataFrame({ "y": y, "ds": x })



"""from sklearn import preprocessing

scaler = preprocessing.MinMaxScaler(feature_range=(0, 1))
normed = scaler.fit_transform(np.array(df["y"]).reshape(-1, 1))

# the output is an array of arrays, so tidy the dimensions
#dataframe["data"] = [round(i[0], 2) for i in normed]
df["y"] = [i[0] for i in normed]"""





train, test = Utils.train_test_split(df, 0.75)

"""last_timestamp, last_data = train.iloc[-1]["timestamp"], train.iloc[-1]["data"]

diff = abs(int(train.iloc[-1]["data"] - train.iloc[-10]["data"]))
print("DIFF " + str(diff))

forecast_data = []
forecast_timestamp = []
for i in range(0, len(test)):
    modifier = random.randint(-200, 200) / 10000 + 1
    #modifier = random.randint(-diff, diff) / 100
    last_data *= modifier
    last_timestamp += 1

    forecast_data.append(last_data)
    forecast_timestamp.append(last_timestamp)


forecast_data = Preprocessor.smooth_data(forecast_data, 20)"""



"""timestamp_res = np.array(train["timestamp"]).reshape(-1, 1)
timestamp_res_test = np.array(test["timestamp"]).reshape(-1, 1)

svm_regression_model = svm.SVR(kernel='poly', degree=5, C=1.0, epsilon=0.2)
svm_regression_model.fit(timestamp_res, train["data"])
forecast_data = svm_regression_model.predict(timestamp_res_test)
forecast_timestamp = []

last_timestamp, last_data = train.iloc[-1]["timestamp"], train.iloc[-1]["data"]

for i in range(0, len(test)):
    forecast_timestamp.append(last_timestamp + i)"""


"""from sktime.datasets import load_airline
from sktime.forecasting.base import ForecastingHorizon
from sktime.forecasting.model_selection import temporal_train_test_split
from sktime.forecasting.theta import ThetaForecaster
from sktime.performance_metrics.forecasting import mean_absolute_percentage_error

fh = ForecastingHorizon(test.index, is_relative=False)
forecaster = ThetaForecaster(sp=70)
forecaster.fit(train["y"])
y_pred = forecaster.predict(fh)
print(y_pred)

plt.plot(train["ds"], train["y"])
plt.plot(test["ds"], test["y"])
plt.plot(y_pred)

plt.show()"""

"""from sklearn.ensemble import GradientBoostingRegressor

GR = GradientBoostingRegressor(n_estimators = 10, max_depth = 1, random_state = 1)
gmodel = GR.fit(np.array(train["ds"]).reshape(-1, 1), train["y"])
g_predict = gmodel.predict(np.array(test["ds"]).reshape(-1, 1))

print(g_predict)
"""


import pandas as pd
import matplotlib.pyplot as plt
from xgboost import XGBRegressor
from skforecast.ForecasterAutoreg import ForecasterAutoreg


# Download data
# ==============================================================================
url = ('https://raw.githubusercontent.com/JoaquinAmatRodrigo/skforecast/master/data/h2o_exog.csv')
data = pd.read_csv(url, sep=',', header=0, names=['date', 'y'])

# Data preprocessing
# ==============================================================================
data['date'] = pd.to_datetime(data['date'], format='%Y/%m/%d')
data = data.set_index('date')
data = data.asfreq('MS')

steps = 36
data_train = data.iloc[:-steps, :]
data_test  = data.iloc[-steps:, :]

# Create and fit forecaster
# ==============================================================================
forecaster = ForecasterAutoreg(
                    regressor = XGBRegressor(),
                    lags = 8
             )

forecaster.fit(y=data['y'], exog=data[['exog_1', 'exog_2']])
forecaster

ddd = forecaster.predict(steps=10, exog=data_test[['exog_1', 'exog_2']])

plt.plot(ddd)
plt.plot(data)
plt.show()