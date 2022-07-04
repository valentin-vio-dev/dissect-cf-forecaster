from predictors.arima_predictor import ArimaPredictor
from predictors.prophet_predictor import ProphetPredictor
from preprocessor import Preprocessor
from utils import Utils


class Predictor:
    def __init__(self):
        self.preprocessor = Preprocessor()

    def compute(self, data):
        preprocessed = self.preprocessor.process(data["data"], data["chunk_size"], data["smooth"])
        original = self.preprocessor.process(data["data"], data["chunk_size"], 0)
        train, test = Utils.train_test_split(preprocessed)

        predictor = None
        if data["predictor"] == "ARIMA":
            predictor = ArimaPredictor(train, test, original)
        elif data["predictor"] == "PROPHET":
            predictor = ProphetPredictor(train, test)
        elif data["predictor"] == "LTSM":
            #predictor = LTSMPredictor(train, test)
            pass
        prediction = predictor.predict()
        
        return {"data": prediction, "data_size": len(prediction)}
