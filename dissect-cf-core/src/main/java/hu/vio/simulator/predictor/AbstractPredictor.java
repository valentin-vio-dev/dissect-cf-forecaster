package hu.vio.simulator.predictor;

import hu.vio.simulator.ComputeNodeData;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPredictor {
    private final ArrayList<ComputeNodeData> history;

    public AbstractPredictor() {
        this.history = new ArrayList<>();
    }

    public void addData(ComputeNodeData data) {
        history.add(data);
    }

    public void clearHistory() {
        history.clear();
    }

    public abstract List<Double> predict(Predictor predictor);

    public abstract String getName();

    public ArrayList<ComputeNodeData> getHistory() {
        return history;
    }
}
