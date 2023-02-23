from predictors.arima_predictor import ArimaPredictor
from predictors.prophet_predictor import ProphetPredictor
from predictors.ltsm_predictor import LTSMPredictor
from preprocessor import Preprocessor
from utils import Utils
from logger import Logger


class Predictor:
    def __init__(self, config):
        self.config = config

    def compute(self, incoming_data):
        chunk_size = incoming_data["chunk_size"]
        smooth = incoming_data["smooth"]
        predictor_name = incoming_data["predictor"]
        data = incoming_data["data"]

        preprocessed = Preprocessor.process(data, chunk_size, smooth)
        original = Preprocessor.process(data, chunk_size, 0)
        train, test = Utils.train_test_split(preprocessed)

        if predictor_name == "ARIMA":
            predictor = ArimaPredictor(train, test, original)
        elif predictor_name == "PROPHET":
            predictor = ProphetPredictor(train, test, original)
        elif predictor_name == "LTSM":
            predictor = LTSMPredictor(train, test, original)
        else:
            raise Exception("No predictor was found!")

        Logger.log(f"Predicting... ({predictor.name()})")
        prediction = predictor.predict()

        return {
            "data": prediction,
            "data_size": len(prediction)
        }
