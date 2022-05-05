package hu.u_szeged.inf.fog.simulator.iot;

import hu.u_szeged.inf.fog.simulator.application.Application;

/**
 * Distance strategy always installs the device on the nearest application.
 */
public class DistanceDeviceStrategy extends DeviceStrategy {

    /**
     * Constructor calls the installation process.
     * @param d The device which needs to be installed/paired.
     */
    public DistanceDeviceStrategy() {}

    /**
     * The method prefers the closest application.
     */
    @Override
    public void install() {
    	double minDistance = Double.MAX_VALUE;
        Application nearestApp = null;
        for (Application app: Application.allApplication) {
            if (app.canJoin &&  d.geoLocation.calculateDistance(app.computingAppliance.geoLocation)<minDistance) {
                minDistance = d.geoLocation.calculateDistance(app.computingAppliance.geoLocation);
                nearestApp = app;
            }
        }
        
        if (nearestApp == null) {
        	System.err.println("There is no possible application for the data transfer!");
        }
        
        this.chosenApplication  = nearestApp;
    }

}