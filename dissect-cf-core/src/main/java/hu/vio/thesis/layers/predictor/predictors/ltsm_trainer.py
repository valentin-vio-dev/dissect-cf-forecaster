import traceback

import numpy as np
import pandas as pd
from sklearn.preprocessing import MinMaxScaler

from thesis.layers.predictor.preprocessor import Preprocessor
from thesis.layers.predictor.utils import Utils

import tensorflow as tf
import json


#{'datasetLocation': 'D:/dev/dataset.csv', 'modelOutput': 'D:/model.h5', 'windowSize': 50, 'smoothing': 20, 'epochs': 10, 'optimizer': 'adam', 'lossFunction': 'mse', 'feature': 'memory'}
class LTSMTrainer:
    def __init__(self, config):
        self.config = config
        self.dataframe = None
        self.X_train = None
        self.y_train = None
        self.model = None

    def load_dataset(self):
        df = pd.read_csv(self.config["datasetLocation"], sep=";")
        df = df.drop(df.columns.difference([self.config["feature"]]), 1, inplace=False)
        df[self.config["feature"]] = df[self.config["feature"]].apply(Utils.replace_commas).astype(float).tolist()
        df[self.config["feature"]] = df[self.config["feature"]].apply(Utils.replace_commas).astype(float).tolist()
        df[self.config["feature"]] = Preprocessor.smooth_data(df[self.config["feature"]].values, self.config["smoothing"])
        df = df.rename(columns={self.config["feature"]: 'data'})

        data = df["data"].values
        timestamp = [i for i in range(0, len(data))]
        dataframe = pd.DataFrame({"value": data}, index=timestamp)

        sc = MinMaxScaler(feature_range=(0, 1))
        dataframe = sc.fit_transform(dataframe.to_numpy())
        dataframe = pd.DataFrame(dataframe, columns=["value"])

        self.dataframe = dataframe

    def make_data_to_window(self, data, windows_size):
        X, y = [], []
        for i in range(len(data) - windows_size):
            X.append(data[i:i + windows_size])
            y.append(data[i + windows_size])
        return np.array(X), np.array(y)

    def prepare_data(self):
        self.X_train, self.y_train = self.make_data_to_window(self.dataframe["value"].values, self.config["windowSize"])

    def set_model(self):
        model = tf.keras.models.Sequential()
        model.add(tf.keras.layers.LSTM(units=50, return_sequences=True, input_shape=(self.config["windowSize"], 1)))
        model.add(tf.keras.layers.Dropout(0.2))
        model.add(tf.keras.layers.LSTM(units=50, return_sequences=True))
        model.add(tf.keras.layers.Dropout(0.2))
        model.add(tf.keras.layers.LSTM(units=50, return_sequences=True))
        model.add(tf.keras.layers.Dropout(0.2))
        model.add(tf.keras.layers.LSTM(units=50))
        model.add(tf.keras.layers.Dropout(0.2))
        model.add(tf.keras.layers.Dense(units=1))
        model.summary()
        self.model = model

    def start_training(self):
        self.model.compile(optimizer=self.config["optimizer"], loss=self.config["lossFunction"])
        self.model.fit(self.X_train, self.y_train, epochs=self.config["epochs"], verbose=1)

    def save_model(self):
        self.model.save(self.config["modelOutput"])
        path = Utils.replace_all(self.config["modelOutput"], "\\", "/")
        path = path.split("/")[0:-1]
        path = "/".join(path)

        try:
            json_object = json.dumps(self.config)
            with open(path + "/model_config.json", "w") as f:
                f.write(json_object)
        except Exception as e:
            traceback.print_exc()