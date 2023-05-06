package hu.vio.thesis.layers.application.refactored.predictors;

public class ForecasterAutoregPredictor extends Predictor {
    public ForecasterAutoregPredictor(int chunkSize, int smooth, double trainSize) {
        super("FORECASTER_AUTOREG", chunkSize, smooth, trainSize);
    }
}
