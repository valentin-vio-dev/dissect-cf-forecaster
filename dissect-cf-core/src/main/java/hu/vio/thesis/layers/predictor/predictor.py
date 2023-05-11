from preprocessor import Preprocessor
from predictors.forecaster_autoreg_predictor import ForecasterAutoregPredictor
from predictors.arima_predictor import ArimaPredictor
from thesis.layers.predictor.predictors.holt_winters_predictor import HoltWintersPredictor
from thesis.layers.predictor.predictors.ltsm_predictor import LTSMPredictor
from thesis.layers.predictor.types import AppType, Command
from utils import Utils
import csv
from logger import Logger


class Predictor:
    def __init__(self, app, config):
        self.app = app
        self.config = config
        self.config["pred_id"] = -1

    def make_stationary(self, values):
        stationary_data = [values[0]]
        for i in range(1, len(values)):
            stationary_data.append(values[i] - values[i - 1])
        stationary_data[0] = stationary_data[1]
        return stationary_data

    def compute(self, incoming_data):
        chunk_size = incoming_data["message"]["chunk_size"]
        smooth = incoming_data["message"]["smoothing"]
        predictor_name = incoming_data["message"]["predictor"]
        features = incoming_data["message"]["features"]
        self.config["pred_id"] = self.config["pred_id"] + 1

        feature_data_list = []
        for feature in features:
            #feature["values"] = self.make_stationary(feature["values"])
            preprocessed = Preprocessor.process(feature["values"], chunk_size, smooth, True, True)
            original = Preprocessor.process(feature["values"], chunk_size, 0, True, True)
            train, test = Utils.train_test_split(preprocessed, incoming_data["message"]["train_size"])
            feature_data_list.append({
                "feature": feature["name"],
                "original": original,
                "train": train,
                "test": test
            })

        if self.config["pred_id"] == 0:
            with open(f"{self.config['outputLocation']}/error_metrics.csv", "w", newline="") as file:
                writer = csv.writer(file, delimiter=";")

                row = ["Prediction ID"]
                for feature in feature_data_list:
                    for error_metric in ["rmse", "mae", "mse"]:
                        row.append(feature["feature"] + "__" + error_metric)
                writer.writerow(row)

        if predictor_name == "ARIMA":
            predictor = ArimaPredictor(self.config, feature_data_list)
        elif predictor_name == "HOLT_WINTERS":
            predictor = HoltWintersPredictor(self.config, feature_data_list)
        elif predictor_name == "FORECASTER_AUTOREG":
            predictor = ForecasterAutoregPredictor(self.config, feature_data_list)
        elif predictor_name == "LTSM":
            predictor = LTSMPredictor(self.config, feature_data_list)
        else:
            raise Exception("No predictor was found!")

        self.app.add_message(AppType.PREDICTOR, "LOG", "predictor", f"Predicting... ({predictor.get_name()})")
        prediction = predictor.predict()
        self.app.add_message(AppType.PREDICTOR, "LOG", "predictor", f"Predicting done!")
        self.app.add_message(AppType.PREDICTOR, "LOG", "predictor", str(prediction)[:100])
        #self.app.add_message(AppType.PREDICTOR, Command.DATA, "predictor-rmse", predictor["rmse"])

        return prediction
