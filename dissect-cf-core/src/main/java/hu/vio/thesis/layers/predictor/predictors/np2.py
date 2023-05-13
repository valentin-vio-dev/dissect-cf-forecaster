import math
import random

import numpy as np
from matplotlib import pyplot as plt
from sklearn import svm
from sklearn.linear_model import LinearRegression

import matplotlib.pyplot as plt
import pandas as pd
from sklearn.preprocessing import MinMaxScaler

from thesis.layers.predictor.preprocessor import Preprocessor

from neuralprophet import NeuralProphet

from thesis.layers.predictor.utils import Utils
from prophet.serialize import model_to_json, model_from_json
import pickle
import json
import math

def prepare_data(data, windows_size):
    X, y = [], []
    for i in range(len(data) - windows_size):
        X.append(data[i:i + windows_size])
        y.append(data[i + windows_size])
    return np.array(X), np.array(y)


def calcualte_rmse(actual, prediction):
    sum = 0

    for i in range(0, len(actual)):
        sum += ((actual[i] - prediction[i]) ** 2) / len(actual)
    return math.sqrt(sum)

def replace_commas(cell):
    return str(cell).replace(",", ".")

def get_dataframe():
    df = pd.read_csv("D:\dev\dissect-cf\dissect-cf-core\src\main\java\hu/vio/thesis/layers\predictor/dataset/CA_Kecskemet_Airport.csv", sep=";")
    df = df.drop(columns=['MEMORY', 'TOTAL_PROC_POWER'])
    df["LOAD_OF_RESOURCE"] = df["LOAD_OF_RESOURCE"].apply(replace_commas).astype(float).tolist()
    df["LOAD_OF_RESOURCE"] = Preprocessor.smooth_data(df["LOAD_OF_RESOURCE"].values, 20)
    df = df.rename(columns={'LOAD_OF_RESOURCE': 'data'})
    df = df[200:200+2000]

    data = df["data"].values
    timestamp = [i for i in range(0, len(data))]
    dataframe = pd.DataFrame({"y": data, "ds": timestamp})

    """dates = []
    start_date = "01/01/1900"
    for i in range(0, len(dataframe)):
        end_date = pd.to_datetime(start_date) + pd.DateOffset(days=i)
        end_date = str(end_date).split(" ")[0]
        dates.append(end_date)
    dataframe["ds"] = dates"""

    sc = MinMaxScaler(feature_range=(0, 1))
    dataframe["y"] = sc.fit_transform(dataframe[["y"]])

    return dataframe




WINDOW = 20

dframe = get_dataframe()
train_dataframe, test_dataframe = Utils.train_test_split(dframe, 0.75)

X_train, y_train = np.array(train_dataframe["ds"].values).reshape(-1, 1), train_dataframe["y"].values
X_test, y_test = np.array(test_dataframe["ds"].values).reshape(-1, 1), test_dataframe["y"].values

model = svm.SVR(kernel='rbf')
model.fit(X_train, y_train)

pred = model.predict(X_test)
print(len(pred))



"""preds = []
for i in range(0, len(X_test)):
    pred = model.predict([X_test[i]])
    preds.append(pred)"""

#x1 = [i for i in range(len(x))]
#x2 = [x1[-1] + i for i in range(len(y_test))]
#x3 = [x1[-1] + i for i in range(len(preds))]


plt.plot(np.array(X_train).flatten(), y_train, color="r")
plt.plot(np.array(X_test).flatten(), y_test, color="b")
plt.plot(np.array(X_test).flatten(), pred, color="g")
#plt.plot(x2, y_test, color="b")
#plt.plot(x3, preds, color="g", linestyle="dashed")
#plt.axvline(x=len(y_train), color="black")
plt.show()



"""def normalize(data):
    return (data - np.min(data)) / (np.max(data) - np.min(data))

t = [(i/100) + (random.random() / 3) for i in range(0, 1000)]
data = [math.sin(i) for i in t]


#data = [(i ** 2) for i in range(0, 100)]
#data = normalize(data)
print(data)
x, y = prepare_data(data, WINDOW)
print(x)


svm_regression_model = svm.SVR(kernel='rbf')
svm_regression_model.fit(x, y)
"""

"""preds = []
for i in range(0, len(data) - WINDOW):
    pred = svm_regression_model.predict([data[i:i+WINDOW]])
    preds.append(pred)
    print(pred)

plt.plot([i for i in range(len(y))], y, color="r")
plt.plot([i for i in range(len(preds))], preds, color="g")
plt.show()"""