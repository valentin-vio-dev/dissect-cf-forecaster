import math
import traceback
from abc import abstractmethod, ABC

import matplotlib.pyplot as plt
import io
import base64
import csv

from thesis.layers.predictor.preprocessor import Preprocessor
from thesis.layers.predictor.utils import Utils


class IPredictor(ABC):
    def __init__(self, name, config, feature_data_list):
        self.name = name
        self.config = config
        self.feature_data_list = feature_data_list
        self.prediction = None

    @abstractmethod
    def make_prediction(self, config, train, test):
        pass

    def write_to_csv(self, predictions):
        with open(f"{self.config['outputLocation']}/error_metrics.csv", "a", newline="") as file:
            writer = csv.writer(file, delimiter=";")

            row = [self.config["pred_id"]]
            for pred in predictions:
                row.append(str(pred["rmse"]).replace(".", ","))
                row.append(str(pred["mae"]).replace(".", ","))
                row.append(str(pred["mse"]).replace(".", ","))
            writer.writerow(row)

    def calcualte_rmse(self, actual, prediction):
        sum = 0

        test_vals = actual["data"].values
        pred_vals = prediction["data"].values

        for i in range(0, len(actual)):
            sum += ((test_vals[i] - pred_vals[i]) ** 2) / len(actual)
        return math.sqrt(sum)

    def calcualte_mae(self, actual, prediction):
        sum = 0

        test_vals = actual["data"].values
        pred_vals = prediction["data"].values

        for i in range(0, len(actual)):
            sum += abs(test_vals[i] - pred_vals[i])
        return (1 / len(actual)) * sum

    def calcualte_mse(self, actual, prediction):
        sum = 0

        test_vals = actual["data"].values
        pred_vals = prediction["data"].values

        for i in range(0, len(actual)):
            sum += (test_vals[i] - pred_vals[i]) ** 2
        return (1 / len(actual)) * sum

    def predict(self):
        base64_image = None

        try:
            predictions = []
            for feature_data in self.feature_data_list:
                prediction = self.make_prediction(
                    self.config,
                    feature_data["train"].copy(),
                    feature_data["test"].copy()
                )

                pred = {
                    "feature": feature_data["feature"],
                    "prediction": prediction,
                    "original": feature_data["original"],
                    "train": feature_data["train"],
                    "test": feature_data["test"],
                    "rmse": self.calcualte_rmse(feature_data["test"].copy(), prediction.copy()),
                    "mae": self.calcualte_mae(feature_data["test"].copy(), prediction.copy()),
                    "mse": self.calcualte_mse(feature_data["test"].copy(), prediction.copy()),
                }
                predictions.append(pred)

            try:
                base64_image = self.create_plot(predictions)
            except Exception as e:
                print(f"Error: {str(e)}")
                traceback.print_exc()

            self.write_to_csv(predictions)

        except Exception as e:
            print("Error while prediction!")
            print(e)

        return {
            "image": base64_image,
            "avg_rmse": self.calculate_average_error(predictions, "rmse"),
            "avg_mae": self.calculate_average_error(predictions, "mae"),
            "avg_mse": self.calculate_average_error(predictions, "mse"),
            "predictions": predictions
        }

    def calculate_average_error(self, predictions, metric):
        avg = 0
        for pred in predictions:
            avg += pred[metric]
        return avg / len(predictions)

    def find_nearest_upper_square_number(self, value):
        for i in range(value, 1_000):
            if int(math.sqrt(i)) ** 2 == i:
                return i

        return None

    def create_plot(self, predictions):
        if len(predictions) == 1:
            prediction = predictions[0]

            fig, ax = plt.subplots()

            ax.plot(
                prediction["original"]["timestamp"].values,
                prediction["original"]["data"].values,
                color="lightgray",
                label="Original"
            )
            ax.plot(
                prediction["train"]["timestamp"].values,
                prediction["train"]["data"].values,
                color="b",
                label="Train"
            )
            ax.plot(
                prediction["test"]["timestamp"].values,
                prediction["test"]["data"].values,
                color="r",
                label="Test"
            )
            ax.plot(
                prediction["prediction"]["timestamp"].values,
                prediction["prediction"]["data"].values,
                color="g",
                label="Prediction",
                linestyle="dashed"
            )
            ax.axvline(prediction["train"]["timestamp"].values[-1], color="black")
            ax.xaxis.set_label_position("top")
            ax.set_xlabel(prediction["feature"])
            ax.grid()
            ax.legend(loc="lower right")
        elif len(predictions) == 2:
            fig, axs = plt.subplots(2)

            for idx, prediction in enumerate(predictions):
                axs[idx].plot(
                    prediction["original"]["timestamp"].values,
                    prediction["original"]["data"].values,
                    color="lightgray",
                    label="Original"
                )
                axs[idx].plot(
                    prediction["train"]["timestamp"].values,
                    prediction["train"]["data"].values,
                    color="b",
                    label="Train"
                )
                axs[idx].plot(
                    prediction["test"]["timestamp"].values,
                    prediction["test"]["data"].values,
                    color="r",
                    label="Test"
                )
                axs[idx].plot(
                    prediction["prediction"]["timestamp"].values,
                    prediction["prediction"]["data"].values,
                    color="g",
                    label="Prediction",
                    linestyle="dashed"
                )
                axs[idx].axvline(prediction["train"]["timestamp"].values[-1], color="black")
                axs[idx].xaxis.set_label_position("top")
                axs[idx].set_xlabel(prediction["feature"])
                axs[idx].grid()
                axs[idx].legend(loc="lower right")
        else:
            cols = int(math.sqrt(self.find_nearest_upper_square_number(len(predictions))))
            rows = cols
            fig, axs = plt.subplots(rows, cols)

            i, j = 0, 0
            for idx, prediction in enumerate(predictions):
                axs[i][j].plot(
                    prediction["original"]["timestamp"].values,
                    prediction["original"]["data"].values,
                    color="lightgray",
                    label="Original"
                )
                axs[i][j].plot(
                    prediction["train"]["timestamp"].values,
                    prediction["train"]["data"].values,
                    color="b",
                    label="Train"
                )
                axs[i][j].plot(
                    prediction["test"]["timestamp"].values,
                    prediction["test"]["data"].values,
                    color="r",
                    label="Test"
                )
                axs[i][j].plot(
                    prediction["prediction"]["timestamp"].values,
                    prediction["prediction"]["data"].values,
                    color="g",
                    label="Prediction",
                    linestyle="dashed"
                )
                axs[i][j].axvline(prediction["train"]["timestamp"].values[-1], color="black")
                axs[i][j].xaxis.set_label_position("top")
                axs[i][j].set_xlabel(prediction["feature"])
                axs[i][j].grid()
                axs[i][j].legend(loc="lower right")

                j += 1

                if j == cols and idx != 0:
                    i += 1
                    j = 0

            for idx in range(len(predictions), cols ** 2):
                axs[i][j].text(0.5, 0.5, "Empty Placeholder", ha="center", va="center")

                j += 1

                if j == cols and idx != 0:
                    i += 1
                    j = 0

        if len(predictions) == 1:
            fig.set_size_inches(7, 7)
        elif len(predictions) == 2:
            fig.set_size_inches(7, 15)
        else:
            fig.set_size_inches(20, 20)

        if self.config["saveStandalone"]:
            plt.savefig(f"{self.config['outputLocation']}/images/{self.name}_{self.config['pred_id']}")

        return self.get_base64_image_string()

    def save_standalone_image(self):
        plt.savefig(Utils.create_and_get_output_directory(self.name.lower()) + "/" + Utils.get_current_date())

    def get_base64_image_string(self):
        io_string_bytes = io.BytesIO()
        plt.savefig(io_string_bytes, format="jpg")
        io_string_bytes.seek(0)
        return f"data:image/jpeg;base64,{base64.b64encode(io_string_bytes.read()).decode()}"

    def get_name(self):
        return self.name
