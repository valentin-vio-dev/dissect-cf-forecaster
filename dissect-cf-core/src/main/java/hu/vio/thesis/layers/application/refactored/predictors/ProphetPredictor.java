package hu.vio.thesis.layers.application.refactored.predictors;

public class ProphetPredictor extends Predictor {
    public ProphetPredictor(int chunkSize, int smooth, double trainSize) {
        super("PROPHET", chunkSize, smooth, trainSize);
    }
}
