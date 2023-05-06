package hu.vio.thesis.layers.application.refactored.predictors;

public class ArimaPredictor extends Predictor {
    public ArimaPredictor(int chunkSize, int smooth, double trainSize) {
        super("ARIMA", chunkSize, smooth, trainSize);
    }
}
