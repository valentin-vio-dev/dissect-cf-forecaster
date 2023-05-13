package hu.vio.thesis.layers.application.refactored.predictors;

public class LinearPredictor extends Predictor {
    public LinearPredictor(int chunkSize, int smooth, double trainSize) {
        super("LINEAR_REGRESSION", chunkSize, smooth, trainSize);
    }
}
