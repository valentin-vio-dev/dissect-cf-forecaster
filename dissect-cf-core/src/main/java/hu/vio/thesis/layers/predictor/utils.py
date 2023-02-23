import math
import os
from datetime import datetime


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
