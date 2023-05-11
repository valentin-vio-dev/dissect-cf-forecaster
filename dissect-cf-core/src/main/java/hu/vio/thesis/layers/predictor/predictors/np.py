import matplotlib.pyplot as plt
import pandas as pd
from sklearn.preprocessing import MinMaxScaler

from thesis.layers.predictor.preprocessor import Preprocessor

from neuralprophet import NeuralProphet

from thesis.layers.predictor.utils import Utils
from prophet.serialize import model_to_json, model_from_json
import pickle
import json

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
    dataframe = pd.DataFrame({ "y": data, "ds": timestamp })

    dates = []
    start_date = "01/01/1900"
    for i in range(0, len(dataframe)):
        end_date = pd.to_datetime(start_date) + pd.DateOffset(days=i)
        end_date = str(end_date).split(" ")[0]
        dates.append(end_date)
    dataframe["ds"] = dates

    sc = MinMaxScaler(feature_range=(0, 1))
    dataframe["y"] = sc.fit_transform(dataframe[["y"]])

    return dataframe


df = get_dataframe()

print(df)
train, test = Utils.train_test_split(df, 0.75)
incoming_data = df[1300:1300+256]
looka = incoming_data[:192]
tet = incoming_data[192:]



"""m = NeuralProphet(
    # Disable trend changepoints
    n_changepoints=10,
    # Disable seasonality components
    yearly_seasonality=True,
    weekly_seasonality=True,
    daily_seasonality=True,
    # Add the autogression
    n_lags=10,
)
metrics = m.fit(train, freq="D")"""
with open('neuralprophet_model.pkl', 'rb') as f:
    m = pickle.load(f)
m.restore_trainer()
#forecast = m.predict(tet)

print(len(tet))
future = m.make_future_dataframe(looka, periods=64, n_historic_predictions=len(tet))
forecast = m.predict(future)
print(len(forecast))

x1 = [i for i in range(0, len(looka))]
x2 = [x1[-1] + i for i in range(0, len(tet))]
plt.plot(x1, looka["y"].values, color="r")
plt.plot(x2, tet["y"].values, color="b")
plt.plot(x2, forecast["yhat1"], linestyle="dashed", color="g")
plt.show()

print(len(looka))
print(len(tet))
print(forecast['yhat1'].values)

#with open('neuralprophet_model.pkl', "wb") as f:
#    pickle.dump(m, f)