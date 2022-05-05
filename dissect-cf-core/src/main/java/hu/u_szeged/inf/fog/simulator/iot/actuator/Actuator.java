package hu.u_szeged.inf.fog.simulator.iot.actuator;

import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;
import hu.u_szeged.inf.fog.simulator.iot.Device;

public class Actuator{

    private long delay;
    
    public Device device;
    
    public static int actuatorEventCounter;

    /**
     * TODO: delay?
     */
    public Actuator(long delay) {
        this.delay = delay;
    }

    public void executeEvent(final ActuatorEvent event) {

        if(event != null) {
            new DeferredEvent(this.delay) {
                @Override
                protected void eventAction() {
                    event.actuate(device);
                    actuatorEventCounter++;
                }
            };
        }
    }
    
    public void executeSingleEvent(final ActuatorEvent event, final Device device, long delay) {
        if(event != null) {
            new DeferredEvent(delay) {
                @Override
                protected void eventAction() {
                    event.actuate(device);
                    actuatorEventCounter++;
                }
            };
        }
    }
}
