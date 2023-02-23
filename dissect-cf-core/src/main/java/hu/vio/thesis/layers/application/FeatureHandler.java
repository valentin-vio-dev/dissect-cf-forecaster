package hu.vio.thesis;


import java.util.HashMap;
import java.util.Map;

public class FeatureHandler {
    private Map<String, IFeatureEntity> features;

    public FeatureHandler() {
        features = new HashMap<>();
    }

    public FeatureHandler addFeature(String name, IFeatureEntity featureEntity) {
        String featureId = Utils.makeUniformIDString(name);
        features.put(featureId, featureEntity);
        return this;
    }

    public Map<String, IFeatureEntity> getFeatures() {
        return features;
    }
}