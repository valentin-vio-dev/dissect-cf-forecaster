import pandas as pd
from sklearn.preprocessing import MinMaxScaler
import matplotlib.pyplot as plt
import numpy as np
from thesis.layers.predictor.preprocessor import Preprocessor
from thesis.layers.predictor.utils import Utils
import tensorflow as tf;

def replace_commas(cell):
    return str(cell).replace(",", ".")

#Chatgpt
def prepare_data(data, window_size):
    X, y = [], []
    for i in range(len(data)-window_size):
        window = data[i:(i+window_size)]
        X.append(window)
        y.append(data[i+window_size])
    X = np.array(X)
    y = np.array(y)
    return X, y

def create_dataset(X, y, time_steps=1):
    Xs, ys = [], []
    for i in range(len(X) - time_steps):
        v = X.iloc[i:(i + time_steps)].values
        Xs.append(v)
        ys.append(y.iloc[i + time_steps])
    return np.array(Xs), np.array(ys)


def create_model(X_train):
    model = tf.keras.models.Sequential()
    model.add(tf.keras.layers.LSTM(units=128, return_sequences=True, input_shape=(X_train.shape[1], X_train.shape[2])))
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


def fit_model(model, X_train, y_train):
    model.compile(
        loss='mean_squared_error',
        optimizer=tf.keras.optimizers.Adam(0.001)
    )

    model.fit(
        X_train, y_train,
        epochs=20,
        batch_size=16,
        validation_split=0.1,
        verbose=1,
        shuffle=False
    )


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


    sc = MinMaxScaler(feature_range=(0, 1))
    dataframe = sc.fit_transform(dataframe.to_numpy())
    dataframe = pd.DataFrame(dataframe, columns=['value'])
    return dataframe



dataframe = get_dataframe()

train, test = Utils.train_test_split(dataframe, 0.75)
#X_train, y_train = create_dataset(train, train["value"], 1)
#X_test, y_test = create_dataset(test, test["value"], 1)

X_train, y_train = create_dataset(train, train["value"], 1)
X_test, y_test = create_dataset(test, test["value"], 1)


model = create_model(X_train)
fit_model(model, X_train, y_train)
y_pred = model.predict(X_test)

print(y_pred)
print(X_train.shape, y_train.shape)

plt.plot(train, label="history")
plt.plot(test, label="true")
plt.plot(np.arange(len(y_train), len(y_train) + len(y_test)), y_pred, 'r', label="prediction")
plt.ylabel('Value')
plt.xlabel('Time Step')
plt.legend()
plt.show()

#print(y_pred)



