import pandas as pd
import numpy as np
from sklearn import preprocessing
from scipy.signal import savgol_filter


class Preprocessor:

    @staticmethod
    def process(data, chunk_size=256, smooth=20, drop_overflow=True, scale=True):
        timestamp = [i for i in range(0, len(data))]

        data_obj = {"data": data, "timestamp": timestamp}
        dataframe = pd.DataFrame(data_obj)



        if smooth > 0:
            dataframe["data"] = savgol_filter(dataframe["data"].values, 40, 5)
            #dataframe["data"] = Preprocessor.smooth_data(dataframe["data"].values, smooth)

        if drop_overflow:
            if len(dataframe["data"]) > chunk_size:
                dataframe = dataframe.iloc[-chunk_size:]

        if scale:
            scaler = preprocessing.MinMaxScaler(feature_range=(0, 1))
            normed = scaler.fit_transform(np.array(dataframe["data"]).reshape(-1, 1))

            # the output is an array of arrays, so tidy the dimensions
            #dataframe["data"] = [round(i[0], 2) for i in normed]
            dataframe["data"] = [i[0] for i in normed]

        # prevent zero or lower
        dd = []
        for i in range(0, len(dataframe["data"].values)):
            dd.append(dataframe["data"].values[i] + 0.0000001)
        dataframe["data"] = dd

        dataframe.reset_index(drop=True, inplace=True)
        return dataframe

    @staticmethod
    def smooth_data(data, smooth):
        tmp = data.copy()
        for s in range(0, smooth):
            for i in range(1, len(tmp) - 1):
                tmp[i] = (tmp[i - 1] + tmp[i + 1]) / 2
        return tmp
