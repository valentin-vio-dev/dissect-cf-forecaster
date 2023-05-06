package hu.u_szeged.inf.fog.simulator.physical;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.specialized.IaaSEnergyMeter;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.util.CloudLoader;
import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.iot.mobility.GeoLocation;
import hu.u_szeged.inf.fog.simulator.loaders.ApplianceModel;
import hu.u_szeged.inf.fog.simulator.loaders.ApplicationModel;
import hu.u_szeged.inf.fog.simulator.loaders.NeigboursModel;

/**
 * This class represents a computing node by including an IaaS service.
 * This service is utilized by IoT applications.
 */
public class ComputingAppliance {

    public static int GID = 0;
    public int id;

    /**
     * A list for collecting all generated objects so far.
     */
    public static List < ComputingAppliance > allComputingAppliance = new ArrayList < ComputingAppliance > ();

    /**
     * It represents the actual position of this node.
     */
    public GeoLocation geoLocation;

    /**
     * A reference to the parent computing node.
     */
    public ComputingAppliance parentNode;

    /**
     * An IaaS service ensures the PMs to create virtualized IoT applications on them.
     */
    public IaaSService iaas;

    /**
     * A list for all IoT application which run on this IaaS service.
     */
    public List < Application > applicationList;

    /**
     * The adjacent nodes which belong to the same cluster with this node.
     */
    public List < ComputingAppliance > neighborList;

    /**
     * Name of the node.
     */
    public String name;

    /**
     * The energy consumed by this node.
     */
    public double energyConsumption;

    /**
     * True, if the energy calculation is on.
     */
    public boolean readEnergy;

    /**
     * Helper variable which ensures that the energy measurement subscriptions happens only once.
     */
    private boolean alreadySubForEnergy;

    /**
     * The cover physical neighborhood of the node from where  it can receive data. Measured in meters.
     */
    public double range;
    /**
     * It constructs a computing node to serve IoT applications and to ensure the tree/graph topology of the fog-cloud system. 
     * @param loadfile It if offered to load the IaaS service from file.
     * @param name Name of the node.
     * @param geoLocation Gives the position of the physical device
     * @param readEnergy True, if the energy calculation is on.
     * @param range
     */
    public ComputingAppliance(String loadfile, String name, GeoLocation geoLocation, boolean readEnergy, double range) throws Exception {
        this.iaas = CloudLoader.loadNodes(loadfile);
        this.name = name;
        this.applicationList = new ArrayList < Application > ();
        this.neighborList = new ArrayList < ComputingAppliance > ();
        this.geoLocation = geoLocation;
        this.range = range;
        this.readEnergy = readEnergy;
        ComputingAppliance.allComputingAppliance.add(this);
        this.id = PhysicalMachine.GID++;
    }

    /**
     * The constructor is unable to handle the adjacent nodes, 
     * thus the neighbor connection must set up after all objects exist.
     * @param appliances the nodes which belong to the same cluster.
     */
    public void addNeighbor(ComputingAppliance...appliances) {
        for (ComputingAppliance ca: appliances) {
        	if(!this.neighborList.contains(ca))
        		this.neighborList.add(ca);
            if(!ca.neighborList.contains(this))
            	ca.neighborList.add(this);
        }
    }

    public void setParentNode(ComputingAppliance ca) {
        this.parentNode = ca;
    }

    /**
     * The constructor is unable to set the applications running on this node,
     * thus this method sets up them and also turn on the energy measurement, if necessary.
     * @param applications the applications which run on this node (on this IaaS service).
     */
    public void addApplication(Application...applications) {
        for (Application app: applications) {
            app.setComputingAppliance(this);
            this.applicationList.add(app);
            if (this.alreadySubForEnergy == false && this.readEnergy) {
                this.alreadySubForEnergy = true;
                this.readEnergy();
            }
        }
    }

    /**
     * Finds the smallest application frequency to ensure fine-grained energy measurement.
     */
    public long findMinFreqApp() {
        long min = Long.MAX_VALUE;
        for (Application app: this.applicationList) {
            if (app.getFrequency() < min) {
                min = app.getFrequency();
            }
        }
        return min;
    }

    /**
     * Checks if there is at least one application, which is still subscribed.
     */
    private boolean activeApplications() {
        for (Application app: this.applicationList) {
            if (app.isSubscribed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * It sets up the latency between two nodes.
     * @param that One of the nodes.
     * @param latency The other one.
     */
    public void setLatency(ComputingAppliance that, int latency) {
        this.iaas.repositories.get(0).addLatencies(that.iaas.repositories.get(0).getName(), latency);
        that.iaas.repositories.get(0).addLatencies(this.iaas.repositories.get(0).getName(), latency);
    }

    /**
     * It turns on the energy measurement with the calculated
     */
    public void readEnergy() {
        final IaaSEnergyMeter iaasEnergyMeter = new IaaSEnergyMeter(this.iaas);
        final ArrayList < Long > readingtime = new ArrayList < Long > ();
        final ArrayList < Double > readingpm = new ArrayList < Double > ();
        class DataCollector extends Timed {
            public void start() {
                subscribe(findMinFreqApp());
            }
            public void stop() {
                unsubscribe();
            }
            @Override
            public void tick(final long fires) {
                readingtime.add(fires);
                readingpm.add(iaasEnergyMeter.getTotalConsumption());
                if (activeApplications()) {
                    this.stop();
                    iaasEnergyMeter.stopMeter();
                    energyConsumption = readingpm.get(readingpm.size() - 1);
                }
            }
        }
        final DataCollector dc = new DataCollector();
        iaasEnergyMeter.startMeter(this.findMinFreqApp(), true);
        dc.start();
    }

    /**
     * The actual load of the IaaS service in percentage.
     */
    public double getLoadOfResource() {
        double usedCPU = 0.0;
        for (VirtualMachine vm: this.iaas.listVMs()) {
            if (vm.getResourceAllocation() != null) {
                usedCPU += vm.getResourceAllocation().allocated.getRequiredCPUs();
            }
        }
        double requiredCPUs = this.iaas.getRunningCapacities().getRequiredCPUs();
        return requiredCPUs > 0 ? usedCPU / requiredCPUs * 100 : 0;
    }
    
	private static ComputingAppliance getComputingApplianceByName(String name) {
		for(ComputingAppliance ca : ComputingAppliance.allComputingAppliance) {
			if(ca.name.equals(name)) {
				return ca;
			}
		}
		return null;
		
	}
	
    public static void loadAppliancesXML(String sourcefile, Map < String, String > iaasLoader) {
        try {
            for (ApplianceModel am: ApplianceModel.loadAppliancesXML(sourcefile)) {
                ComputingAppliance ca = new ComputingAppliance(iaasLoader.get(am.file), am.name, new GeoLocation(am.latitude, am.longitude), true, am.range);
                for (ApplicationModel a: am.getApplications()) {
                    ca.addApplication(new Application(a.freq, a.tasksize, a.instance, a.name, a.countOfInstructions, a.threshold, a.strategy, a.canJoin));
                }
            }
            for (ApplianceModel am: ApplianceModel.loadAppliancesXML(sourcefile)) {
                ComputingAppliance ca = getComputingApplianceByName(am.name);
                if(am.neighbours !=null) {
                    for (NeigboursModel nam : am.neighbours) {
                        ComputingAppliance friend = getComputingApplianceByName(nam.name);
                        if (Boolean.parseBoolean(nam.parent) && nam.parent != null) {
                            ca.setParentNode(friend);
                            ca.setLatency(friend, nam.latency);
                        } else {
                            ca.addNeighbor(friend);
                            ca.setLatency(friend, nam.latency);
                        }
                    }	
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }
}