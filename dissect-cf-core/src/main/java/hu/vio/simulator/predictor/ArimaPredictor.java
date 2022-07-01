package hu.vio.simulator.predictor;

import java.util.List;

public class ArimaPredictor extends AbstractPredictor {

    public ArimaPredictor() {
        super();
    }

    @Override
    public List<Double> predict(Predictor predictor) {
        predictor.executeScript();
        return null;
    }

    @Override
    public String getName() {
        return "ARIMA";
    }
}
