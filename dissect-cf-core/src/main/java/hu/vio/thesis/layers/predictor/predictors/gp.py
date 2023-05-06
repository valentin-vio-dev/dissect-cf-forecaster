import math
import random

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.preprocessing import MinMaxScaler

from thesis.layers.predictor.preprocessor import Preprocessor


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
    df = df[100:100+2000]

    data = df["data"].values
    timestamp = [i for i in range(0, len(data))]
    dataframe = pd.DataFrame({ "value": data }, index=timestamp)


    #sc = MinMaxScaler(feature_range=(0, 1))
    #dataframe = sc.fit_transform(dataframe.to_numpy())
    dataframe = pd.DataFrame(dataframe, columns=['value'])
    return dataframe

# Adatok betöltése
df = get_dataframe()

# Adatok normalizálása
scaler = MinMaxScaler()
scaled_data = scaler.fit_transform(df["value"].values.reshape(-1, 1))

# Adatok előkészítése az LSTM modell számára
n_steps_in = 192
n_steps_out = 64

def prepare_data(data, n_steps_in, n_steps_out):
    X, y = [], []
    for i in range(len(data)-n_steps_in-n_steps_out):
        X.append(data[i:i+n_steps_in])
        y.append(data[i+n_steps_in:i+n_steps_in+n_steps_out])
    X = np.array(X)
    y = np.array(y)
    return X, y

X_train, y_train = prepare_data(scaled_data, n_steps_in, n_steps_out)

# LSTM modell létrehozása
"""model = tf.keras.models.Sequential([
    tf.keras.layers.LSTM(50, activation='relu', input_shape=(n_steps_in, 1)),
    tf.keras.layers.Dense(n_steps_out)
])"""

model = tf.keras.models.Sequential()
model.add(tf.keras.layers.LSTM(units=50, return_sequences=True, input_shape=(n_steps_in, 1)))
model.add(tf.keras.layers.Dropout(0.2))
model.add(tf.keras.layers.LSTM(units=50, return_sequences=True))
model.add(tf.keras.layers.Dropout(0.2))
model.add(tf.keras.layers.LSTM(units=50, return_sequences=True))
model.add(tf.keras.layers.Dropout(0.2))
model.add(tf.keras.layers.LSTM(units=50))
model.add(tf.keras.layers.Dropout(0.2))
model.add(tf.keras.layers.Dense(units=n_steps_out))
model.summary()

# Modell összeállítása
model.compile(optimizer='adam', loss='mse')

# Modell tanítása
model.fit(X_train, y_train, epochs=10, verbose=1)

# Előrejelzés másik adaton
incoming_data = df["value"].values[200:200+256]
looka = incoming_data[:192]
tet = incoming_data[192:]

new_data = np.array(looka)  # 192 elemű adatsorozat
scaled_new_data = scaler.transform(new_data.reshape(-1, 1))

X_test = scaled_new_data
predicted = model.predict(X_test)

# Eredmények visszaskálázása
predicted = scaler.inverse_transform(predicted)

# Az utolsó 64 érték kiválasztása az előrejelzésből
predicted_values = predicted[-1]

# Az eredmények megjelenítése
print(len(tet))
print(len(predicted_values))
print(calcualte_rmse(tet, predicted_values))


x1 = [i for i in range(0, len(looka))]
x2 = [x1[-1] + i for i in range(0, len(tet))]
plt.plot(x1, looka)
plt.plot(x2, tet)
plt.plot(x2, predicted_values)
plt.show()