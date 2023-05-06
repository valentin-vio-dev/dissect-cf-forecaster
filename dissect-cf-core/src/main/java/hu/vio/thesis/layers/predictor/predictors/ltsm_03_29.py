from os import listdir
from os.path import isfile, join
import pandas as pd
import numpy as np
import math
from sklearn.preprocessing import MinMaxScaler
from keras.models import Sequential
from keras.layers import Dense, LSTM
import matplotlib.pyplot as plt
import tensorflow as tf


def smooth_data(data, smooth):
    tmp = data.copy()
    for s in range(0, smooth):
        for i in range(1, len(tmp) - 1):
            tmp[i] = (tmp[i - 1] + tmp[i] + tmp[i + 1]) / 3
    return tmp


def load_data():
    pass


def prepare_data():
    pass


def create_model(input_shape):  # window size
    model = tf.keras.models.Sequential()
    model.add(tf.keras.layers.LSTM(units=50, return_sequences=True, input_shape=(input_shape, 1)))
    model.add(tf.keras.layers.Dropout(0.2))
    model.add(tf.keras.layers.LSTM(units=50, return_sequences=True))
    model.add(tf.keras.layers.Dropout(0.2))
    model.add(tf.keras.layers.LSTM(units=50, return_sequences=True))
    model.add(tf.keras.layers.Dropout(0.2))
    model.add(tf.keras.layers.LSTM(units=50))
    model.add(tf.keras.layers.Dropout(0.2))
    model.add(tf.keras.layers.Dense(units=1))
    model.summary()

    return model


def train_model(X_train_in, y_train_in, input_shape=60, epochs=1, batch_size=32):
    model = create_model(input_shape)
    model.compile(optimizer='adam', loss='mean_squared_error')
    model.fit(X_train_in, y_train_in, epochs=epochs, batch_size=batch_size)


"""def predict_data(model, data, prediction_length):
    dataset_total = pd.concat([data_to_train['Tester'], data_to_test['Tester']], axis=0)
    inputs = dataset_total[len(dataset_total) - len(data_to_test) - 60:].values
    # print(dataset_total[len(dataset_total) - len(data_to_test) - 60:])
    inputs = inputs.reshape(-1, 1)
    inputs = sc.transform(inputs)
    X_test = []

    for i in range(60, 500 + 60):
        X_test.append(inputs[i - 60:i, 0])
    X_test = np.array(X_test)
    X_test = np.reshape(X_test, (X_test.shape[0], X_test.shape[1], 1))
    
    predicted_stock_price = model.predict(X_test)
    predicted_stock_price = sc.inverse_transform(predicted_stock_price)"""

data = pd.read_csv(
    "D:\dev\dissect-cf\dissect-cf-core\src\main\java\hu/vio/thesis\layers\predictor/train_dataset/CA_Kecskemet Airport_PM_6.csv",
    sep=";")
data = data.iloc[0:2000]
data = data.applymap(lambda x: str(x).replace(",", ".")).astype(float)
data["Tester"] = smooth_data(data["Tester"].values, 20)

data_to_train = data[:1500]
data_to_test = data[1500:]

training_set = data_to_train.iloc[:, 3:4].values
real_stock_price = data_to_test.iloc[:, 3:4].values

sc = MinMaxScaler(feature_range=(0, 1))
training_data_scaled = sc.fit_transform(training_set)

X_train = []
y_train = []
for i in range(60, 1500):
    X_train.append(training_data_scaled[i - 60:i, 0])
    y_train.append(training_data_scaled[i, 0])
X_train, y_train = np.array(X_train), np.array(y_train)

train_model(X_train, y_train)

"""plt.figure(figsize=(12, 6))
plt.plot(real_stock_price, color='black', label='Apple Stock Price')
plt.plot(predicted_stock_price, color='red', label='Predicted Apple Stock Price')
plt.title('Apple Stock Price Prediction')
plt.xlabel('Time')
plt.xlabel('Apple Stock Price')
plt.legend()
plt.show()
"""