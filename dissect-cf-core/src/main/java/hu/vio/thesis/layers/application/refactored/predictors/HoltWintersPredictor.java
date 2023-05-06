package hu.vio.thesis.layers.application.refactored.predictors;

public class HoltWintersPredictor extends Predictor{
    public HoltWintersPredictor(int chunkSize, int smooth, double trainSize) {
        super("HOLT_WINTERS", chunkSize, smooth, trainSize);
    }
}
