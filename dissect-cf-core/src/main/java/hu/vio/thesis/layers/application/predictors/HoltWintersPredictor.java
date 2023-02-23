package hu.vio.thesis.layers.application.predictors;

public class HoltWinters extends Predictor{
    public HoltWinters(int chunkSize, int smooth, double trainSize) {
        super("HOLT_WINTERS", chunkSize, smooth, trainSize);
    }
}
