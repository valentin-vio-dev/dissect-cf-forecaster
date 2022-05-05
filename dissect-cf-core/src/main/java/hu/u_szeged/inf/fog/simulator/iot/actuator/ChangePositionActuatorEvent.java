package hu.u_szeged.inf.fog.simulator.iot.actuator;

import hu.u_szeged.inf.fog.simulator.iot.Device;
import hu.u_szeged.inf.fog.simulator.iot.mobility.GeoLocation;

public class ChangePositionActuatorEvent implements ActuatorEvent {

    private GeoLocation newLocation;
    
    public static long changePositionEventCounter;

    public ChangePositionActuatorEvent(GeoLocation geoLocation) {
        this.newLocation = geoLocation;
    }

    @Override
    public void actuate(Device device) {
    	device.geoLocation = newLocation;
        changePositionEventCounter++;
    }
}