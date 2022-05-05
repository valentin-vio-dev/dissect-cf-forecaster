package hu.u_szeged.inf.fog.simulator.iot.actuator;

import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.iot.Device;

public class ChangeNodeActuatorEvent implements ActuatorEvent {

    Application from, to;
    
    public static long changeNodeEventCounter;

    public ChangeNodeActuatorEvent(Application from, Application to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void actuate(Device device) {
    	device.app.deviceList.remove(device);
    	
        device.app = to;
        device.nodeRepository = to.computingAppliance.iaas.repositories.get(0); 
        to.deviceList.add(device);

        device.mc.localDisk.addLatencies(device.app.computingAppliance.iaas.repositories.get(0).getName(), device.latency + 
        		(int) (device.geoLocation.calculateDistance(device.app.computingAppliance.geoLocation)/1000));
                
        changeNodeEventCounter++;
       
        if (!device.app.isSubscribed()) {
            try {
            	device.app.restartApplication();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
