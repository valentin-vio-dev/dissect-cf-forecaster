package hu.vio.thesis.layers.application.refactored;

import hu.vio.thesis.layers.application.Utils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Feature {
    private String name;
    private List<Double> values;
    private List<Double> history;

    public Feature(String name) {
        this.name = name;
        this.values = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    public Feature(String name, boolean format) {
        this.name = format ? Utils.makeUniformIDString(name) : name;
        this.values = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    abstract public double compute();

    public String getName() {
        return name;
    }

    public void addValue(double value) {
        values.add(value);
    }

    public List<Double> getValues() {
        return values;
    }

    public List<Double> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "name='" + name + '\'' +
                ", length=" + values.size() +
                ", values=" + Utils.strToStrSpace(Arrays.toString(values.toArray()), 50) +
                '}';
    }

    public JSONObject toJSON() {
        return new JSONObject().
                put("name", name).
                put("values", values);
    }
    public void clear() {
        for (Double d: values) {
            history.add(d);
        }
        values = new ArrayList<>();
    }
}
