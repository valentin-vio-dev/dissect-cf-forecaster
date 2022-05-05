package hu.u_szeged.inf.fog.simulator.iot;

import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.iot.actuator.ChangeNodeActuatorEvent;
import hu.u_szeged.inf.fog.simulator.iot.actuator.ConnectToNodeActuatorEvent;
import hu.u_szeged.inf.fog.simulator.iot.actuator.DisconnectFromNodeActuatorEvent;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

/**
 * The goal of this class to pair a device to an application. A few default strategy has already been implemented, 
 * but you can define your strategy, for this purpose you should implement the InstallionStrategy interface. 
 */
public abstract class DeviceStrategy {

	public Device d;
	
	Application chosenApplication;
	
    /**
     * 	This method needs to be overridden by realizing the actual policy/logic to determine 
     * which application is appropriate to pair the device to.
     * @param d The device which should be paired.
     */
    public void update() {
    	this.handleDisconnectFromNode();
    	this.install();
    	this.handleConnectToNode();
    }
    
    public abstract void install();
       
    private void handleDisconnectFromNode() {
         if(d.app != null) {
             if(calculateLatency(d.app.computingAppliance) > (2*d.latency) ||
            		 d.geoLocation.calculateDistance(d.app.computingAppliance.geoLocation) > d.app.computingAppliance.range) {
            	 Station s = (Station) d;
                 if (s.actuator != null) {
                     s.actuator.executeSingleEvent(new DisconnectFromNodeActuatorEvent(), s, 0);
                 } else {
                    //@ VIO_REMOVED_COMMENT @  System.out.println("Actuator must be set in order to disconnect from fog node!");
                    //@ VIO_REMOVED_COMMENT @  System.exit(0);
                 }
             } 
         }
    }
    
    private void handleConnectToNode() {        
        if (d.app != null) {
        	if (this.chosenApplication!=null && this.chosenApplication!= d.app) {
             if(d.actuator != null) {
                d.actuator.executeSingleEvent(new ChangeNodeActuatorEvent(d.app, this.chosenApplication), d, 0);
             } else {
            	//@ VIO_REMOVED_COMMENT @  System.out.println("Actuator must be set in order to disconnect from fog node!");
                //@ VIO_REMOVED_COMMENT @  System.exit(0);
             }
        	} 
        } else {
            if(d.actuator != null) {
                if(this.chosenApplication != null) {
                    d.actuator.executeSingleEvent(new ConnectToNodeActuatorEvent(this.chosenApplication), d, 0);
                }
                else {
                    //@ VIO_REMOVED_COMMENT @ System.out.println("There is no chosen application for the device");
                }
            } else {
            	//@ VIO_REMOVED_COMMENT @ System.out.println("Actuator must be set in order to disconnect from fog node!");
                System.exit(0);
            }
        }
    }
    
    private double calculateLatency(ComputingAppliance ca) {
        return d.latency + (int) (d.geoLocation.calculateDistance(ca.geoLocation)/1000);
    }
}