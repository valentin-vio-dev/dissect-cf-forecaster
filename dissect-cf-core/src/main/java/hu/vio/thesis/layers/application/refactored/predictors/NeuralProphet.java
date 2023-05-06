package hu.vio.thesis.layers.application.refactored.predictors;

public class NeuralProphet extends Predictor {
    public NeuralProphet(int chunkSize, int smooth, double trainSize) {
        super("NEURAL_PROPHET", chunkSize, smooth, trainSize);
    }
}
