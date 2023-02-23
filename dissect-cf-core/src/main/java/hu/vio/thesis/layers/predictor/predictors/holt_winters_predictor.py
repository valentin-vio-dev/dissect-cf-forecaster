import pandas as pd
from statsmodels.tsa.holtwinters import SimpleExpSmoothing, Holt, ExponentialSmoothing
import plotly.graph_objects as go
from scipy.signal import lfilter


from thesis.layers.predictor.utils import Utils

dataframe = pd.read_csv("D:\dev\dissect-cf\dissect-cf-core\src\main\java\hu/vio/thesis/layers\predictor/dataset/CA_Kecskemet_Airport.csv", sep=";")
dataframe = dataframe[5000:5000+256]

def replace_commas(cell):
    return str(cell).replace(",", ".")


def plot_func(forecast1: list[float],
              forecast2: list[float],
              forecast3: list[float],
              title: str) -> None:
    """Function to plot the forecasts."""
    fig = go.Figure()
    fig.add_trace(go.Scatter(x=train['TIME'], y=train['LOAD_OF_RESOURCE'], name='Train'))
    fig.add_trace(go.Scatter(x=test['TIME'], y=test['LOAD_OF_RESOURCE'], name='Train'))
    fig.add_trace(go.Scatter(x=test['TIME'], y=forecast1, name='Simple'))
    fig.add_trace(go.Scatter(x=test['TIME'], y=forecast2, name="Holt's Linear"))
    fig.add_trace(go.Scatter(x=test['TIME'], y=forecast3, name='Holt Winters'))
    fig.update_layout(template="simple_white", font=dict(size=18), title_text=title,
                      width=700, title_x=0.5, height=400, xaxis_title='Date',
                      yaxis_title='Passenger Volume')
    return fig.show()



dates = []
start_date = "01/01/1900"
for i in range(0, len(dataframe.index)):
    end_date = pd.to_datetime(start_date) + pd.DateOffset(months=i)
    end_date = str(end_date).split(" ")[0]
    dates.append(end_date)
dataframe["TIME"] = dates

dataframe["LOAD_OF_RESOURCE"] = dataframe["LOAD_OF_RESOURCE"].apply(replace_commas).astype(float).tolist()
n = 10
b = [1.0 / n] * n
a = 1
dataframe["LOAD_OF_RESOURCE"] = lfilter(b, a, dataframe["LOAD_OF_RESOURCE"].values)
dataframe = dataframe.drop(columns=['MEMORY', 'TOTAL_PROC_POWER'])




print(dataframe)
train, test = Utils.train_test_split(dataframe)


model_simple = SimpleExpSmoothing(train["LOAD_OF_RESOURCE"]).fit(optimized=True)
forecasts_simple = model_simple.forecast(len(test))

model_holt = Holt(train["LOAD_OF_RESOURCE"], damped_trend=True).fit(optimized=True)
forecasts_holt = model_holt.forecast(len(test))

model_holt_winters = ExponentialSmoothing(train["LOAD_OF_RESOURCE"], trend='add', seasonal='add', seasonal_periods=60).fit(optimized=True)
forecasts_holt_winters = model_holt_winters.forecast(len(test))



plot_func(forecasts_simple, forecasts_holt, forecasts_holt_winters,  "Holt-Winters Exponential Smoothing")
