package hu.u_szeged.inf.fog.simulator.iot;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;
import hu.mta.sztaki.lpds.cloud.simulator.util.SeedSyncer;
import hu.u_szeged.inf.fog.simulator.iot.actuator.Actuator;
import hu.u_szeged.inf.fog.simulator.iot.actuator.ChangePositionActuatorEvent;
import hu.u_szeged.inf.fog.simulator.iot.mobility.GeoLocation;
import hu.u_szeged.inf.fog.simulator.iot.mobility.MobilityStrategy;
import hu.u_szeged.inf.fog.simulator.iot.mobility.RandomMobilityStrategy;
import hu.u_szeged.inf.fog.simulator.loaders.DeviceModel;
import hu.u_szeged.inf.fog.simulator.physical.MicroController;
import hu.u_szeged.inf.fog.simulator.util.MicrocontrollerPowerTransitionGenerator;

/**
 *	This represent an IoT device realization with a fixed physical position.
 *	Initially this class was planned to model IoT weather station-like behavior. 
 */
public class Station extends Device {

	/**
	 * It describes the movement pattern of the device.
	 */
	private MobilityStrategy mobilityStrategy;
		
    /**
     * Constructs an IoT device. At the creation of a station a random value is defined between 0-5, 
     * which is added to the start time and the stop time of the device to avoid a too circular behavior.
     * It directly chooses the IoT application which the device can communicate with and 
     * initialize the station depending on the start time and the delay value.  
     * @param startTime When the IoT device starts working (in milliseconds).
     * @param stopTime When the IoT device stops working (in milliseconds).
     * @param fileSize The size of one measurement (in bytes).
     * @param sensorCount It tells us how many sensors the current IoT device has.
     * @param deviceStrategy The policy to determine which IoT application the current IoT device communicates with.
     * @param freq The time interval between two sensor measurement (in milliseconds). 
     * @param geoLocation The position of the device.
     * @param mc The physical properties of the device, i.e. CPU, memory, network.
     * @param latency The delay for a data packet to travel (in milliseconds).
     * @param sensorFreq The length of one measurement of a sensor (in milliseconds). 
     * @param readEnergy Tells if the energy measuring is on or off.
     * @param mobilityStrategy The strategy which describes the movements of the device.
     */
    public Station(long startTime, long stopTime, long fileSize, int sensorCount, DeviceStrategy deviceStrategy, long freq, GeoLocation geoLocation, 
    		MicroController mc, int latency, long sensorFreq, boolean readEnergy, MobilityStrategy mobilityStrategy, Actuator actuator) {
        long delay = Math.abs(SeedSyncer.centralRnd.nextLong() % 6) * 60 * 1000; // TODO: fix this delay value
        this.startTime = startTime + delay;
        this.stopTime = stopTime + delay;
        this.fileSize = fileSize * sensorCount;
        this.sensorCount = sensorCount;
        this.deviceStrategy = deviceStrategy;
        this.mc = mc;
        this.freq = freq;
        this.latency = latency;
        this.sensorFreq = sensorFreq;
        this.readEnergy = readEnergy;
        this.geoLocation = geoLocation;
        this.mobilityStrategy = mobilityStrategy;
        this.deviceStrategy.d = this;
        this.actuator = actuator;
    	this.actuator.device = this;
        this.startMeter();
        Device.allDevices.add(this);
    }

	/**
     * The recurring event of the device handles the data generating and sending process.
     */
    @Override
    public void tick(long fires) {
        if (Timed.getFireCount() < stopTime && Timed.getFireCount() >= startTime) {
            new Sensor(this, this.sensorFreq);
            try {
                this.mc.setStateToMetering();
            } catch (NetworkException e) {
                e.printStackTrace();
            }
        }
        
        if(mobilityStrategy != null) {
        	GeoLocation location = mobilityStrategy.move(freq);
            if(location != null && actuator != null) {
                actuator.executeEvent(new ChangePositionActuatorEvent(location));
            }           
        }

        this.deviceStrategy.update();        

        if (this.mc.localDisk.getFreeStorageCapacity() == this.mc.localDisk.getMaxStorageCapacity() && Timed.getFireCount() > stopTime) {
            this.stopMeter();
        }
       
        try {
            if (this.nodeRepository.currState.equals(Repository.State.RUNNING)) {
                this.startCommunicate();
            }
        } catch (NetworkException e) {
            e.printStackTrace();
        }

       if (!this.app.isSubscribed()) {
            try {
                this.app.restartApplication();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static DeviceStrategy strategy(String strategy) {
    	DeviceStrategy ds = null;
        if (strategy.equals("cost")) {
        	ds = new CostDeviceStrategy();
        } else if (strategy.equals("distance")) {
            ds = new DistanceDeviceStrategy();
        } else if (strategy.equals("fuzzy")) {
            ds = new FuzzyDeviceStrategy();
        } else if (strategy.equals("random")) {
            ds = new RandomDeviceStrategy();
        } else if (strategy.equals("runtime")) {
           ds = new RuntimeDeviceStrategy();
        } else {
        	System.err.println("WARNING: the device strategy called " + strategy + " does not exist!");
            System.exit(0);
        }
        return ds;
    }
    
	public static void loadDevicesXML(String sourcefile) {
		try {
			for (DeviceModel dm: DeviceModel.loadDeviceXML(sourcefile)) {	
				// TODO: remove for
				for (int j= 0; j < 100; j++) {
					  HashMap < String, Integer > latencyMap = new HashMap < String, Integer > ();
		    		  EnumMap<PowerTransitionGenerator.PowerStateKind, Map<String, PowerState>> transitions;
					try {
						transitions = MicrocontrollerPowerTransitionGenerator.generateTransitions(dm.minpower, dm.idlepower, dm.maxpower, 0, 0);
						Map < String, PowerState > cpuTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.host);
	    			      Map < String, PowerState > stTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.storage);
	    			      Map < String, PowerState > nwTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.network);
	    		  MicroController mc = new MicroController(dm.cores, dm.perCorePocessing, dm.ram,
	    		            new Repository(dm.capacity, "mc", dm.maxInBW, dm.maxOutBW, dm.diskBW, latencyMap, stTransitions, nwTransitions),
	    		            dm.onD, dm.offD, cpuTransitions);
	    		  GeoLocation gl = new GeoLocation(dm.latitude,  dm.longitude);
	    		      	 new Station(dm.startTime, dm.stopTime, dm.fileSize, dm.sensorCount, Station.strategy(dm.strategy) , 
	    		      			 dm.freq, gl , mc, dm.latency, 1000, true, 
	    		      			 new RandomMobilityStrategy(gl, dm.speed, dm.radius), new Actuator(1));
					} catch (SecurityException | InstantiationException | IllegalAccessException | NoSuchFieldException
							| IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}