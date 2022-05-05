package hu.vio.simulator;

/**
 * This class represents a feature in compute node.
 */
public class Feature {
    public String name;
    public Object data;
    public boolean export;

    public Feature(String name, Object data, boolean export) {
        this.name = name;
        this.data = data;
        this.export = export;
    }
}