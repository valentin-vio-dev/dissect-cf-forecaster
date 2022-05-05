package hu.u_szeged.inf.fog.simulator.iot;

import hu.u_szeged.inf.fog.simulator.application.Application;

/**
 * Cost-aware strategy prefers the cheapest application.
 */
public class CostDeviceStrategy extends DeviceStrategy {

	/**
	 * Constructor calls the installation process.
	 * @param d The device which needs to be installed/paired.
	 */
    public CostDeviceStrategy() {}

    /**
     * The policy prefers the cheapest application.
     */
    @Override
    public void install() {
        double min = Integer.MAX_VALUE;
        Application chosenApp = null;
        for (Application app: Application.allApplication) {
            if (app.canJoin && app.instance.pricePerTick < min) {
                min = app.instance.pricePerTick;
                chosenApp = app;
            }
        }

        if (chosenApp == null) {
        	System.err.println("There is no possible application for the data transfer!");
            System.exit(0);
        }
      this.chosenApplication =  chosenApp;
    }

}