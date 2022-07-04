package hu.vio.simulator.predictor;

import java.util.List;

public class ProphetPredictor extends AbstractPredictor {

    public ProphetPredictor() {
        super();
    }

    @Override
    public List<Double> predict(Predictor predictor) {
        predictor.send();
        return null;
    }

    @Override
    public String getName() {
        return "PROPHET";
    }
}
