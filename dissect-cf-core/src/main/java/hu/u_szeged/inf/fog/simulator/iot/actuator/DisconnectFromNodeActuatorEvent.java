package hu.u_szeged.inf.fog.simulator.iot.actuator;

import hu.u_szeged.inf.fog.simulator.iot.Device;

public class DisconnectFromNodeActuatorEvent implements ActuatorEvent {
	
    public static long DisconnectFromNodeEventCounter;
    
    public DisconnectFromNodeActuatorEvent(){}

    @Override
    public void actuate(Device device) {
    	device.app.deviceList.remove(device);
    	device.app = null;
        DisconnectFromNodeEventCounter++;
    }
}
