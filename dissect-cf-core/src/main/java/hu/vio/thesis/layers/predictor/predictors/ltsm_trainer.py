from os import listdir
from os.path import isfile, join
import pandas as pd
from preprocessor import Preprocessor
import numpy as np
import math
from sklearn.preprocessing import MinMaxScaler
from keras.models import Sequential
from keras.layers import Dense, LSTM
import matplotlib.pyplot as plt

# 400 - 300 train, 100 test

CHUNK_SIZE = 400
SMOOTH = 20
TRAIN_DATASET_DIR = "D:/_dev/vio/dissect-cf/dissect-cf-core/src/main/java/hu/vio/simulator/predictor/python/train_dataset"
MODEL = None
TRAIN_SIZE = 0.75


def load_and_preprocess_dataset(path):
    files = [f for f in listdir(path) if isfile(join(path, f)) and f.split(".")[-1] == "csv"]
    result = []

    for file in files:
        """file_name = file.split(".")[0]
        dataframe = pd.read_csv("{}\\{}".format(path, file), sep=";")
        data = dataframe["Tester"].apply(lambda x: str(x).replace(",", ".")).astype(float).tolist()
        data = np.array(data).reshape(-1, 1)

        result.append({
            "name": file_name,
            "data": data
        })"""

        file_name = file.split(".")[0]
        dataframe = pd.read_csv("{}\\{}".format(path, file), sep=";")
        dataframe = dataframe.filter(["Tester"])
        dataframe = dataframe["Tester"].apply(lambda x: str(x).replace(",", ".")).astype(float)
        data = np.array(dataframe.to_list()).reshape(-1, 1)

        result.append({
            "name": file_name,
            "data": data,
            "dataframe": dataframe
        })

    return result


def train_model(datasets):
    model = Sequential()
    model.add(LSTM(50, return_sequences=True, input_shape=(CHUNK_SIZE, 1)))
    model.add(LSTM(50, return_sequences=False))
    model.add(Dense(25))
    model.add(Dense(1))

    for ds in datasets:
        # Scale
        scaler = MinMaxScaler(feature_range=(0, 1))
        scaled_data = scaler.fit_transform(ds["data"])

        # Train dataset
        train = scaled_data[0:math.ceil(len(scaled_data) * TRAIN_SIZE), :]
        x_train = []
        y_train = []
        for i in range(CHUNK_SIZE, len(train)):
            x_train.append(train[(i - CHUNK_SIZE):i, 0])
            y_train.append(train[i, 0])
        x_train, y_train = np.array(x_train), np.array(y_train)

        # Test dataset
        test = scaled_data[math.ceil(len(scaled_data) * TRAIN_SIZE) - CHUNK_SIZE:, :]
        x_test = []
        y_test = ds["data"][math.ceil(len(scaled_data) * TRAIN_SIZE):, :]
        for i in range(CHUNK_SIZE, len(test)):
            x_test.append(test[(i - CHUNK_SIZE):i, 0])
        x_test = np.array(x_test)
        x_test = np.reshape(x_test, (x_test.shape[0], x_test.shape[1], 1))

        # Fit model
        model.compile(optimizer="adam", loss="mean_squared_error")
        model.fit(x_train, y_train, batch_size=1, epochs=1)

        # Prediction
        prediction = model.predict(x_test)
        prediction = scaler.inverse_transform(prediction)

        train = ds["dataframe"][:math.ceil(len(scaled_data) * TRAIN_SIZE)]
        test = ds["dataframe"][math.ceil(len(scaled_data) * TRAIN_SIZE):]
        prediction = pd.Series(prediction.flatten())
        prediction.index = test.index

        # Plot
        plt.plot(train, color="b", label="Train")
        plt.plot(test, color="r", label="Test")
        plt.plot(prediction, color="g", label="Prediction", linestyle="dashed")

        plt.legend(loc="upper left")
        plt.grid()
        plt.show()

    #model.save("gdrive/My Drive/train_dataset/my_model.h5")



def save_model(model):
    pass


def main():
    datasets = load_and_preprocess_dataset(TRAIN_DATASET_DIR)
    MODEL = train_model([datasets[0]])
    #save_model(model)


main()
