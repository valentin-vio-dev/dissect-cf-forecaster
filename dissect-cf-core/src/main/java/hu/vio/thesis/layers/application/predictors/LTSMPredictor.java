package hu.vio.thesis.predictors;

public class LTSMPredictor extends Predictor {
    public LTSMPredictor(int chunkSize, int smooth, double trainSize) {
        super("LTSM", chunkSize, smooth, trainSize);
    }
}
