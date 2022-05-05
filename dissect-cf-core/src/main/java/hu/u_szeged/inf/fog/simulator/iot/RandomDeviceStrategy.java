package hu.u_szeged.inf.fog.simulator.iot;

import java.util.ArrayList;
import java.util.List;

import hu.mta.sztaki.lpds.cloud.simulator.util.SeedSyncer;
import hu.u_szeged.inf.fog.simulator.application.Application;

/**
 * Random strategy randomly chooses one available application located run by any nodes.
 */
public class RandomDeviceStrategy extends DeviceStrategy {

    /**
     * Constructor calls the installation process.
     */
    public RandomDeviceStrategy() {}

    /**
     * The random logic is quite simple, it chooses an available application randomly.
     */
    @Override
    public void install() {
        int rnd;
        List < Application > availableApplications = new ArrayList < Application > ();
        for (Application app: Application.allApplication) {
            if (app.canJoin) {
                availableApplications.add(app);
            }
        }

        if (availableApplications.size() > 0) {
            rnd = SeedSyncer.centralRnd.nextInt(availableApplications.size());
            this.chosenApplication = availableApplications.get(rnd);
        } else {
        	System.err.println("There is no possible application for the data transfer!");
            System.exit(0);
        }
    }
}