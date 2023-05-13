package hu.vio.thesis.layers.application.refactored.predictors;

public class RandomForestPredictor extends Predictor {
    public RandomForestPredictor(int chunkSize, int smooth, double trainSize) {
        super("RANDOM_FOREST", chunkSize, smooth, trainSize);
    }
}
