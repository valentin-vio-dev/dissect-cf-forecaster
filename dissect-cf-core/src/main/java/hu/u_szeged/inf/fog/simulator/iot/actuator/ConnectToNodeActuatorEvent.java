package hu.u_szeged.inf.fog.simulator.iot.actuator;

import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.iot.Device;

public class ConnectToNodeActuatorEvent implements ActuatorEvent {

    Application application;
    
    public static long connectToNodeEventCounter;

    public ConnectToNodeActuatorEvent(Application application) {
        this.application = application;
    }
    
    @Override
    public void actuate(Device device) {
        device.app = application;
        device.nodeRepository = application.computingAppliance.iaas.repositories.get(0); 
        application.deviceList.add(device);
        device.mc.localDisk.addLatencies(device.app.computingAppliance.iaas.repositories.get(0).getName(), device.latency + 
        		(int) (device.geoLocation.calculateDistance(device.app.computingAppliance.geoLocation)/1000));     
        connectToNodeEventCounter++;
        if (!device.app.isSubscribed()) {
            try {
            	device.app.restartApplication();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
