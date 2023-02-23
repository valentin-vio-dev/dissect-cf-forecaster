package hu.vio.thesis;

import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComputingNodesHandler implements IDataListener {
    private boolean verbose;
    private List<ComputingNode> computingNodes;
    private FeatureHandler featureHandler;
    private DataNotifier dataNotifier;

    public ComputingNodesHandler(boolean verbose) {
        this.verbose = verbose;
        computingNodes = new ArrayList<>();
        dataNotifier = new DataNotifier(DataNotifier.DataNotifyType.TICK_COUNT, 0, this);
    }

    public void addComputingAppliances(ComputingAppliance... computingAppliances) {
        for (ComputingAppliance computingAppliance: computingAppliances) {
            ComputingNode computingNode = new ComputingNode(computingAppliance);
            computingNode.setFeatureHandler(featureHandler);
            computingNodes.add(computingNode);
        }
    }

    public void setFeatureHandler(FeatureHandler featureHandler) {
        this.featureHandler = featureHandler;
    }

    private void extractAllComputingApplianceData() {
        for (ComputingNode computingNode: computingNodes) {
            for (Map.Entry<String, IFeatureEntity> entry: featureHandler.getFeatures().entrySet()) {
                Object o = entry.getValue().compute(computingNode);
                computingNode.addData(entry.getKey(), o);
            }
        }
    }

    public void exportAllToCSV(String path) {
        for (ComputingNode computingNode: computingNodes) {
            computingNode.exportToCSV(path);
        }
    }

    public void tick() {
        dataNotifier.tick();
    }

    @Override
    public void notifyDataListener() {
        extractAllComputingApplianceData();
    }

    public List<ComputingNode> getComputingNodes() {
        return computingNodes;
    }
}
