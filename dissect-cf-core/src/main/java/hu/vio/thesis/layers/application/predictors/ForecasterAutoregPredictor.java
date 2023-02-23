package hu.vio.thesis.layers.application.predictors;

public class PyAF extends Predictor {
    public PyAF(int chunkSize, int smooth, double trainSize) {
        super("PYAF", chunkSize, smooth, trainSize);
    }
}
