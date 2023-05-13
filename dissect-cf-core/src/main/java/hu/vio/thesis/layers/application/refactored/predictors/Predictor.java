package hu.vio.thesis.layers.application.refactored.predictors;

import hu.vio.thesis.layers.application.*;
import hu.vio.thesis.layers.application.refactored.Feature;
import hu.vio.thesis.layers.application.refactored.FeatureManager;
import hu.vio.thesis.layers.application.refactored.MessageProtocol;
import hu.vio.thesis.layers.application.refactored.SocketClient;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Predictor {
    private static Predictor predictor;
    private final String name;
    private final int chunkSize;
    private final int smooth;
    private final double trainSize;
    private SocketClient socketClient;

    private FeatureManager featureManager;

    protected Predictor(String name, int chunkSize, int smooth, double trainSize) {
        this.name = name;
        this.chunkSize = chunkSize;
        this.smooth = smooth;
        this.trainSize = trainSize;
    }

    public void setFeatureManager(FeatureManager featureManager) {
        this.featureManager = featureManager;
    }

    public void setSocketClient(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    public JSONObject predict() throws Error {
        if (socketClient == null || (socketClient != null && !socketClient.isConnected())) {
            throw new Error("Socket object is not set!");
        }

        JSONObject response = null;
        try {
            if (featureManager.getFeatureValueLength() < chunkSize) {
                socketClient.send(new MessageProtocol(MessageProtocol.Command.LOG, "data-size", featureManager.getFeatureValueLength()));
            } else {
                response = socketClient.send(new MessageProtocol(MessageProtocol.Command.DATA, "data-to-predict", createSocketMessage()));
                featureManager.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public JSONObject createSocketMessage() {
        JSONObject body = new JSONObject();

        body.put("predictor", name);
        body.put("train_size", trainSize);
        body.put("smoothing", smooth);
        body.put("chunk_size", chunkSize);
        body.put("feature_size", featureManager.getFeatures().size());

        List<JSONObject> fs = new ArrayList<>();
        for (Feature feature: featureManager.getFeatures()) {
            System.out.println(feature.getName());
            fs.add(feature.toJSON());
        }
        body.put("features", fs);

        return body;
    }

    private static Predictor getPredictor(String predictorName) {
        int chunkSize = Integer.parseInt(Args.get("chunkSize", "256"));
        int smoothing = Integer.parseInt(Args.get("smoothing", "20"));
        float tranSize = Float.parseFloat(Args.get("trainSize", "0.75"));

        switch (predictorName) {
            case "ARIMA":
                return new ArimaPredictor(chunkSize, smoothing, tranSize);
            /*case "FORECASTER_AUTOREG":
                return new ForecasterAutoregPredictor(chunkSize, smoothing, tranSize);*/
            case "HOLT_WINTERS":
                return new HoltWintersPredictor(chunkSize, smoothing, tranSize);
            case "LTSM":
                return new LTSMPredictor(chunkSize, smoothing, tranSize);
            /*case "NEURAL_PROPHET":
                return new NeuralProphet(chunkSize, smoothing, tranSize);*/
            /*case "PROPHET":
                return new ProphetPredictor(chunkSize, smoothing, tranSize);*/
            case "RANDOM_FOREST":
                return new RandomForestPredictor(chunkSize, smoothing, tranSize);
            case "SVR":
                return new SVRPredictor(chunkSize, smoothing, tranSize);
            case "LINEAR_REGRESSION":
                return new LinearPredictor(chunkSize, smoothing, tranSize);
            default:
                throw new RuntimeException();
        }
    }

    public static Predictor getInstance() {
        if (predictor == null) {
            predictor = getPredictor(Args.get("predictor", "arima").toUpperCase());
        }
        return predictor;
    }
}
