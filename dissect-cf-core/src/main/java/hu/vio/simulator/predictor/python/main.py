import sys
import sqlite3
import os
import matplotlib.pyplot as plt
import pandas as pd
import math
from datetime import datetime
from scipy.signal import lfilter
from arima_predictor import ArimaPredictor


ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = ROOT_DIR + "/pred_database.db"


args = dict()


def train_test_split(dataframe, size_train = 0.75):
  size = len(dataframe.index)
  ts = math.floor(size * size_train)
  return dataframe.iloc[:ts], dataframe.iloc[ts:]


def read_data():
    data = []
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.execute("SELECT * FROM Data")
    for row in cursor:
        seperated = row[1].split("=")
        columns = seperated[0].lower().replace("  ", "").replace(". ", "_").replace(".", "_").replace(" ", "_").split(";")
        values = seperated[1].split(";")

        d = dict()
        for i in range(0, len(columns)):
            d[columns[i]] = float(values[i].replace(",", "."))
        
        data.append(d)
    conn.close()
    return pd.DataFrame(data)


def write_data(data):
    conn = sqlite3.connect(DB_PATH)
    c.executemany("INSERT INTO Pred VALUES (?)", data)
    connection.commit()
    conn.close()


def get_args():
    tmp_args = dict()
    for i in range(1, len(sys.argv)):
        if "=" in sys.argv[i]:
            arg = sys.argv[i].split("=")
            key = arg[0].replace("--", "")
            value = arg[1]
            tmp_args[key] = value
        else:
            key = sys.argv[i].replace("--", "")
            tmp_args[key] = None
    return tmp_args


def preprocess(dataframe):
    df = dataframe.copy()
    df.drop(["load_of_resource", "memory", "total_proc_power"], axis = 1, inplace = True)
    df.rename(columns = {"tester": "data"}, inplace = True)

    n = int(args.get("smooth", 20))
    b = [1.0 / n] * n
    a = 1

    df["data"] = lfilter(b, a, df["data"].values)
    
    if len(df["data"]) > 256:
        df = df.iloc[-256:]
  
    df.reset_index(drop=True, inplace=True)
    df["timestamp"] = [i for i in range(0, len(df["data"]))]

    return df


def get_rmse(test, prediction):
    rmse = 0
    for i in range(0, len(prediction)):
        rmse += ((prediction[i] - test["data"].values[i])  ** 2) / len(prediction)
    return math.sqrt(rmse)


def main():
    global args
    args = get_args()

    if len(args) < 1:
        return

    if "predictor" not in args:
        return

    dataframe = read_data()
    dataframe = preprocess(dataframe)
    train, test = train_test_split(dataframe, float(args.get("train_size", 0.75)))

    predictor = None
    if args["predictor"] == "ARIMA":
        predictor = ArimaPredictor(train, test)
    elif args["predictor"] == "LTSM":
        pass
    elif args["predictor"] == "PROPHET":
        pass
    
    prediction = predictor.predict()
    print("RMSE = " + str(get_rmse(test, prediction)))


    


    

    
    



main()

