package hu.vio.thesis;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

public class Feature {
    private String name;
    private Object value;

    private ComputingAppliance computingAppliance;
    private PhysicalMachine physicalMachine;

    public Feature(String name, Object value, ComputingAppliance computingAppliance, PhysicalMachine physicalMachine) {
        this.name = name;
        this.value = value;
        this.computingAppliance = computingAppliance;
        this.physicalMachine = physicalMachine;
    }


}
