package hu.vio.simulator;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a compute node data at given time.
 */
public class ComputeNodeData {
    /**
     * List for all feature functions.
     */
    public static List<FeatureFunctionTemplate> featureFunctionTemplates = new ArrayList<>();

    /**
     * List for store all compute node data.
     */
    private final List<Feature> featureList;

    private ComputingAppliance computingAppliance;
    private PhysicalMachine machine;

    public ComputeNodeData(ComputingAppliance computingAppliance, PhysicalMachine machine) {
        this.featureList = new ArrayList<>();
        this.computingAppliance = computingAppliance;
        this.machine = machine;

        for (FeatureFunctionTemplate featureFunctionTemplate: featureFunctionTemplates) {
            featureList.add(new Feature(
                            featureFunctionTemplate.name,
                            featureFunctionTemplate.featureFunction.compute(computingAppliance, machine),
                            featureFunctionTemplate.export
                    )
            );
        }
    }

    /**
     * Prints all features and values.
     */
    public void print() {
        for (Feature feature: featureList) {
            Logger.printTitle(StringUtils.capitalize(feature.name), 25);
        }

        Logger.endl();

        for (Feature feature: featureList) {
            if (feature.data instanceof Number) {
                Logger.printCell(String.format("%.4f", (Double) feature.data), 25);
            } else {
                Logger.printCell(feature.data.toString(), 25);
            }
        }

        Logger.endl();
        Logger.line(featureList.size(), 25);
    }

    /**
     * Returns all feature name as a comma separated string.
     */
    public static String getColumns() {
        StringBuilder result = new StringBuilder();

        for (FeatureFunctionTemplate featureFunctionTemplate: featureFunctionTemplates) {
            if (!featureFunctionTemplate.export) {
                continue;
            }

            result.append(featureFunctionTemplate.name).append(";");
        }

        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    /**
     * For add new feature.
     */
    public static void addFeature(String name, boolean export, FeatureFunctionTemplate.FeatureFunction template) {
        for (FeatureFunctionTemplate featureFunctionTemplate: featureFunctionTemplates) {
            if (featureFunctionTemplate.name.equals(name)) {
                return;
            }
        }

        featureFunctionTemplates.add(new FeatureFunctionTemplate(name, template, export));
    }

    public ComputingAppliance getComputingAppliance() {
        return computingAppliance;
    }

    public void setComputingAppliance(ComputingAppliance computingAppliance) {
        this.computingAppliance = computingAppliance;
    }

    public PhysicalMachine getMachine() {
        return machine;
    }

    public void setMachine(PhysicalMachine machine) {
        this.machine = machine;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Feature feature: featureList) {
            if (!feature.export) {
                continue;
            }

            if (feature.data instanceof Number) {
                result.append(String.format("%.4f", (Double) feature.data).replace("\\.", ",")).append(";");
            } else {
                result.append(feature.data.toString()).append(";");
            }
        }

        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public List<Feature> getFeatureList() {
        return featureList;
    }
}
