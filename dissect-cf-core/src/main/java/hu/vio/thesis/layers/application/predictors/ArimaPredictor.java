package hu.vio.thesis.predictors;

public class ArimaPredictor extends Predictor {
    public ArimaPredictor(int chunkSize, int smooth, double trainSize) {
        super("ARIMA", chunkSize, smooth, trainSize);
    }
}
