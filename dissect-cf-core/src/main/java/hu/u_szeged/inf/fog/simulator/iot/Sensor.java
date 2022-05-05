package hu.u_szeged.inf.fog.simulator.iot;

import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;
import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.io.StorageObject;

/**
 * This class represent the concrete data generation method. 
 * With this class we can also simulate the measurement interval as well.
 */
class Sensor extends DeferredEvent {

    /**
     * The device which stores the generated data.
     */
    private Device d;
    
    /**
     * Constructs the delayed sensor object, after that delay the data may be available in the local repository.
     * @param d The device which stores the generated data.
     * @param delay The delay of the environment sensing.
     */
    Sensor(Device d, long delay) {
        super(delay);
        this.d = d;
    }

    /**
     * The method generates a file and stores the data into the local repository. 
     */
    @Override
    protected void eventAction() {
        StorageObject so = new StorageObject(this.d.mc.localDisk.getName() + " " + this.d.fileSize + " " + Timed.getFireCount(), this.d.fileSize, false);
        if (this.d.mc.localDisk.registerObject(so)) {
            this.d.messageCount += 1;
            this.d.sumOfGeneratedData += so.size;
            this.d.mc.setStateToRunning();
        } else {
            try {
                System.err.println("WARNING: Saving data into the local repository is unsuccessful!");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}