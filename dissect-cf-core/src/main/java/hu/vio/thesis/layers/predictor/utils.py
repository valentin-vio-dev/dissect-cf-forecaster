import math
import os
from datetime import datetime
from pathlib import Path

class Utils:

    @staticmethod
    def train_test_split(dataframe, size_train=0.75):
        size = len(dataframe.index)
        ts = math.floor(size * size_train)
        return dataframe.iloc[:ts], dataframe.iloc[ts:]

    @staticmethod
    def get_rmse(test, prediction):
        rmse = 0
        for i in range(0, len(prediction)):
            rmse += ((prediction[i] - test["data"].values[i]) ** 2) / len(prediction)
        return math.sqrt(rmse)

    @staticmethod
    def create_and_get_output_directory(predictor_name):
        directory = os.path.dirname(os.path.abspath(__file__)) + "/images/" + predictor_name
        if not os.path.exists(directory):
            os.makedirs(directory)
        return directory

    @staticmethod
    def get_current_date():
        return datetime.now().strftime("%Y_%m_%d_%H_%M_%S")

    @staticmethod
    def trim_str(text, max_size):
        return text[0:max_size] + "..." if len(text) > max_size else text

    @staticmethod
    def create_repostory(path):
        Path(path).mkdir(parents=True, exist_ok=True)
        Path(path + '/images').mkdir(parents=True, exist_ok=True)
        Path(path + '/config').mkdir(parents=True, exist_ok=True)
        Path(path + '/dataset').mkdir(parents=True, exist_ok=True)


    @staticmethod
    def replace_commas(cell):
        return str(cell).replace(",", ".")

    @staticmethod
    def replace_all(s, c, t):
        result = s
        while c in result:
            result = result.replace(c, t)
        return result

