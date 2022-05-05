package hu.u_szeged.inf.fog.simulator.iot;

import hu.u_szeged.inf.fog.simulator.application.Application;

import java.util.Comparator;

/**
 * Runtime-aware strategy calculates the ratio of the number of connected devices and 
 * the number of physical machines. Finally, it chooses the less loaded application. 
 */
public class RuntimeDeviceStrategy extends DeviceStrategy {

	/**
	 * Constructor calls the installation process.
	 */
    public RuntimeDeviceStrategy() {}

    /**
     * The method prefers the less loaded application. As the load may change with time, a deferred event ensures to
     * use the actual load in the given time.
     */
    @Override
    public void install() {
        chosenApplication = Application.allApplication.stream()
                .filter(application -> application.canJoin)
                .min(Comparator.comparing(application -> application.computingAppliance.getLoadOfResource()))
                .orElse(Application.allApplication.get(0));
    }

}