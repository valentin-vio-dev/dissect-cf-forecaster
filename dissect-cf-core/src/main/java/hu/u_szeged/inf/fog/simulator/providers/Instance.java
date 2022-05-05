package hu.u_szeged.inf.fog.simulator.providers;

import java.util.HashMap;
import javax.xml.bind.JAXBException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.AlterableResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.u_szeged.inf.fog.simulator.loaders.InstanceModel;

/**
 * This class represents a collection of necessary properties for creating virtual machines on IoT applications.
 */
public class Instance {
	
    /**
     * The virtual machine image.
     */
    public VirtualAppliance va;

    /**
     * The name of the instance.
     */
    private String name;

    /**
     * The resource need of the virtual machine.
     */
    public AlterableResourceConstraints arc;

    /**
     * The price of one unit of time.
     */
    public double pricePerTick;

    /**
     * A map for collecting all generated instance objects so far.
     */
    public static HashMap < String, Instance > instances = new HashMap < String, Instance > ();

    /**
     * It initializes the properties to be able create similar VMs.
     * @param va The VM image.
     * @param arc The resource need of the VM.
     * @param pricePerTick The time unit cost of the VM.
     * @param name The name of the VM instance.
     */
    public Instance(VirtualAppliance va, AlterableResourceConstraints arc, double pricePerTick, String name) {
        this.va = va;
        this.arc = arc;
        this.pricePerTick = pricePerTick;
        this.name = name;
        instances.put(this.name, this);
    }

    /**
     * It calculates the actual cost of the VM.
     * @param time The elapsed time.
     */
    public double calculateCloudCost(long time) {
        return time * pricePerTick;
    }
    
	public static void loadInstancesXML(String sourcefile) {
		try {
			for(InstanceModel im : InstanceModel.loadInstancesXML(sourcefile)) {
				Instance i = new Instance(new VirtualAppliance(im.name, im.startupProcess, im.networkLoad, false, im.reqDisk), 
								new AlterableResourceConstraints(im.cpuCores,im.coreProcessingPower,im.ram), im.pricePerTick,im.name);
				instances.put(i.name,i);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}