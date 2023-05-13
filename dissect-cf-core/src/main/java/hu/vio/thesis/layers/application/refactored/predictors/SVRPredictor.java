package hu.vio.thesis.layers.application.refactored.predictors;

public class SVRPredictor extends Predictor {
    public SVRPredictor(int chunkSize, int smooth, double trainSize) {
        super("SVR", chunkSize, smooth, trainSize);
    }
}
