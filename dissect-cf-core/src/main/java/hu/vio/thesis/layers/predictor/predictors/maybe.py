import math
import random

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.preprocessing import MinMaxScaler
from thesis.layers.predictor.preprocessor import Preprocessor
from thesis.layers.predictor.utils import Utils

WINDOWS_SIZE = 50
EPOCHS = 10


def train(dataset_path, train_size, window_size, smooth, epochs, optimizer, loss, features):
    pass

def replace_commas(cell):
    return str(cell).replace(",", ".")

def prepare_data(data, windows_size):
    X, y = [], []
    for i in range(len(data) - windows_size):
        X.append(data[i:i + windows_size])
        y.append(data[i + windows_size])
    return np.array(X), np.array(y)




def get_dataframe():
    df = pd.read_csv("D:\dev\dissect-cf\dissect-cf-core\src\main\java\hu/vio/thesis/layers\predictor/dataset/CA_Kecskemet_Airport.csv", sep=";")
    df = df.drop(columns=['MEMORY', 'TOTAL_PROC_POWER'])
    df["LOAD_OF_RESOURCE"] = df["LOAD_OF_RESOURCE"].apply(replace_commas).astype(float).tolist()
    df["LOAD_OF_RESOURCE"] = Preprocessor.smooth_data(df["LOAD_OF_RESOURCE"].values, 20)
    df = df.rename(columns={'LOAD_OF_RESOURCE': 'data'})
    df = df[100:100+500]

    data = df["data"].values
    timestamp = [i for i in range(0, len(data))]
    dataframe = pd.DataFrame({ "value": data }, index=timestamp)


    sc = MinMaxScaler(feature_range=(0, 1))
    dataframe = sc.fit_transform(dataframe.to_numpy())
    dataframe = pd.DataFrame(dataframe, columns=['value'])
    return dataframe

# Adatok betöltése
df = get_dataframe()


train, test = Utils.train_test_split(df, 0.75)
X_train, y_train = prepare_data(train["value"].values, WINDOWS_SIZE)


model = tf.keras.models.Sequential()
model.add(tf.keras.layers.LSTM(units=50, return_sequences=True, input_shape=(WINDOWS_SIZE, 1)))
model.add(tf.keras.layers.Dropout(0.2))
model.add(tf.keras.layers.LSTM(units=50, return_sequences=True))
model.add(tf.keras.layers.Dropout(0.2))
model.add(tf.keras.layers.LSTM(units=50, return_sequences=True))
model.add(tf.keras.layers.Dropout(0.2))
model.add(tf.keras.layers.LSTM(units=50))
model.add(tf.keras.layers.Dropout(0.2))
model.add(tf.keras.layers.Dense(units=1))
model.summary()

# Modell összeállítása
model.compile(optimizer='adam', loss='mse')

# Modell tanítása
model.fit(X_train, y_train, epochs=EPOCHS, verbose=1)

model.save('final_model.h5')

"""X_test, y_test = prepare_data(test["value"].values[100:100 + 256], WINDOWS_SIZE)
#X_test, y_test = prepare_data([random.random() for i in range(0, 256 + WINDOWS_SIZE)], WINDOWS_SIZE)

predicted = model.predict(X_test)
#predicted = predicted.tolist()[-64:]

x1 = [i for i in range(0, len(y_test))]
x2 = [i for i in range(192, 256)]
x2 = [i for i in range(0, len(predicted))]


plt.plot(x1, y_test.tolist(), 'r', label="test")
plt.plot(x2, predicted, 'g', label="prediction", linestyle='dashed')
plt.legend()
plt.show()"""