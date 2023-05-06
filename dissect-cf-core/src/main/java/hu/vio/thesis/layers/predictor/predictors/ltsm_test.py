from keras.saving.legacy.model_config import model_from_json
from os import listdir
from os.path import isfile, join
import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler
import matplotlib.pyplot as plt

TRAIN_DATASET_DIR = "D:\dev\dissect-cf\dissect-cf-core\src\main\java\hu/vio/thesis\layers\predictor/train_dataset"

def load_and_preprocess_dataset(path):
    files = [f for f in listdir(path) if isfile(join(path, f)) and f.split(".")[-1] == "csv"]
    result = []

    for file in files:
        file_name = file.split(".")[0]
        dataframe = pd.read_csv("{}\\{}".format(path, file), sep=";")
        dataframe = dataframe.iloc[0:1000]
        dataframe = dataframe.filter(["Tester"])
        dataframe = dataframe["Tester"].apply(lambda x: str(x).replace(",", ".")).astype(float)
        data = np.array(dataframe.to_list()).reshape(-1, 1)

        result.append({
            "name": file_name,
            "data": data,
            "dataframe": dataframe
        })

    return result


# load json and create model
json_file = open('model.json', 'r')
loaded_model_json = json_file.read()
json_file.close()
loaded_model = model_from_json(loaded_model_json)
# load weights into new model
loaded_model.load_weights("model.h5")
print("Loaded model from disk")

loaded_model.compile(optimizer="adam", loss="mean_squared_error")
datasets = load_and_preprocess_dataset(TRAIN_DATASET_DIR)


ds = datasets[0]

scaler = MinMaxScaler(feature_range=(0, 1))
scaled_data = scaler.fit_transform(ds["data"])



x_test = np.array(scaled_data[0:400])
#x_test = np.reshape(x_test, (0, 400, 1))
prediction = loaded_model.predict(x_test)
prediction = scaler.inverse_transform(prediction)
prediction = pd.Series(prediction.flatten())
#prediction.index = x_test.index


qq = scaler.inverse_transform(x_test[0:400])
qq = pd.Series(qq.flatten())

plt.plot(qq, color="r", label="Test",)
plt.plot(prediction, color="g", label="Prediction", linestyle="dashed")

plt.legend(loc="upper left")
plt.grid()
plt.show()



