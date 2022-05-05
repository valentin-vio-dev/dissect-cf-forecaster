package hu.vio.simulator;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

/**
 * Class for feature extraction.
 */
public class FeatureFunctionTemplate {
    public interface FeatureFunction {
        Object compute(ComputingAppliance computingAppliance, PhysicalMachine machine);
    }

    public String name;
    public FeatureFunction featureFunction;
    public boolean export;

    public FeatureFunctionTemplate(String name, FeatureFunction featureFunction, boolean export) {
        this.name = name;
        this.featureFunction = featureFunction;
        this.export = export;
    }
}