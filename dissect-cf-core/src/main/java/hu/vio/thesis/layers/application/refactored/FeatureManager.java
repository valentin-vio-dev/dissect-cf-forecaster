package hu.vio.thesis.layers.application.refactored;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class FeatureManager implements IDataListener {
    private static FeatureManager featureManager;
    private List<Feature> features;
    private int featureValueLength;
    private DataNotifier dataNotifier;

    private FeatureManager() {
        this.features = new ArrayList<>();
        this.dataNotifier = new DataNotifier(DataNotifier.DataNotifyType.TICK_COUNT, 0, this);
        this.featureValueLength = 0;
    }

    public void clear() {
        for (Feature feature: features) {
            feature.clear();
        }
        featureValueLength = 0;
    }

    public void computeFeatures() {
        for (Feature feature: features) {
            feature.addValue(feature.compute());
        }

        for (Feature feature: features) {
            //System.out.println(feature.toString());
        }

        featureValueLength++;
    }

    public FeatureManager addFeature(Feature feature) {
        if (!featureExists(feature)) {
            features.add(feature);
        }
        return this;
    }

    public Feature getFeatureByName(String name) {
        for (Feature feature: features) {
            if (feature.getName().equals(name)) {
                return feature;
            }
        }
        return null;
    }

    boolean featureExists(Feature feature) {
        return getFeatureByName(feature.getName()) != null;
    }

    public void exportToCSV(String path) {
        try (PrintWriter printWriter = new PrintWriter(path)) {
            StringBuilder sb = new StringBuilder();

            // Columns
            for (Feature feature: features) {
               sb.append(feature.getName()).append(";");
            }

            sb.append("\n");

            // Values
            for (int i = 0; i < features.get(0).getHistory().size(); i++) {
                for (Feature feature: features) {
                    sb.append(String.format("%.8f", feature.getHistory().get(i)).replace("\\.", ",")).append(";");
                }
                sb.append("\n");
            }

            printWriter.write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FeatureManager getInstance() {
        if (featureManager == null) {
            featureManager = new FeatureManager();
        }
        return featureManager;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public DataNotifier getDataNotifier() {
        return dataNotifier;
    }

    @Override
    public void notifyDataListener() {
        computeFeatures();
    }

    public int getFeatureValueLength() {
        return featureValueLength;
    }
}
