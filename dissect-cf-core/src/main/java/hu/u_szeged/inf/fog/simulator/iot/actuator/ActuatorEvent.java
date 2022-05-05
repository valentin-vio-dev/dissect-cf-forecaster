package hu.u_szeged.inf.fog.simulator.iot.actuator;

import hu.u_szeged.inf.fog.simulator.iot.Device;

public interface ActuatorEvent {

	public void actuate(Device device);

}