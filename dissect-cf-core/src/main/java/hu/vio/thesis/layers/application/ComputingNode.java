package hu.vio.thesis;

import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputingNode {
    private ComputingAppliance computingAppliance;
    private Map<String, List<Object>> data;
    private FeatureHandler featureHandler;

    public ComputingNode(ComputingAppliance computingAppliance) {
        this.computingAppliance = computingAppliance;
    }

    public void setFeatureHandler(FeatureHandler featureHandler) {
        this.featureHandler = featureHandler;
        data = new HashMap<>();
        for (Map.Entry<String, IFeatureEntity> entry: featureHandler.getFeatures().entrySet()) {
            data.put(entry.getKey(), new ArrayList<>());
        }
    }

    public void addData(String key, Object o) {
        data.get(key).add(o);
    }

    public ComputingAppliance getComputingAppliance() {
        return computingAppliance;
    }

    public int getSize() {
        return data.get(featureHandler.getFeatures().keySet().toArray()[0]).size();
    }

    public void printAll() {
        for (Map.Entry<String, List<Object>> entry: data.entrySet()) {
            Logger.log(entry.getKey(), "\t", entry.getValue().toString().substring(0, 100));
        }
        Logger.log("......");
    }

    private String getColumns() {
        StringBuilder result = new StringBuilder();

        for (String label: featureHandler.getFeatures().keySet()) {
            result.append(label).append(";");
        }

        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public void exportToCSV(String path) {
        try (PrintWriter printWriter = new PrintWriter(path + "\\CA_" + computingAppliance.name.replaceAll(" ", "_") + ".csv")) {
            StringBuilder sb = new StringBuilder();

            sb.append(getColumns()).append("\n");

            for (int i = 0; i < getSize(); i++) {
                for (String key: data.keySet()) {
                    Object d = data.get(key).get(i);
                    if (d instanceof Number) {
                        sb.append(String.format("%.4f", (Double) d).replace("\\.", ",")).append(";");
                    } else {
                        sb.append(d.toString()).append(";");
                    }
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("\n");
            }

            printWriter.write(sb.toString());
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
    }

    public String getFormatedName() {
        return Utils.makeUniformIDString(computingAppliance.name);
    }

    public Map<String, List<Object>> getData() {
        return data;
    }
}
