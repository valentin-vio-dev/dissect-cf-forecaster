package hu.u_szeged.inf.fog.simulator.application;

import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;

/**
 * The class represents a daemon service which tries to send the unprocessed data delayed between two application.
 * It happens when the target application is currently not available, e.g. it is unsubscribed.
 */
public class BrokerCheck extends DeferredEvent {

	/**
	 * Where the unprocessed data go to.
	 */
	private Application toApp;
	
	/**
	 * Where the unprocessed data come from.
	 */
	private Application fromApp;
	
	/**
	 * The amount of the unprocessed data.
	 */
	private long unprocessedData;
	
	/**
	 * The time delay when this daemon service tries to send the data again.
	 */
	long delay;
	
	/**
	 * It initializes a daemon service to manage a delayed data transfer between applications.
	 * @param fromApp Where the data come from.
	 * @param toApp Where the data go to.
	 * @param unprocessedData The size of the unprocessed data.
	 * @param delay The next time when the daemon service tries to send the data.
	 */
	public BrokerCheck(Application fromApp, Application toApp,  long unprocessedData, long delay) {
		super(delay);
		this.delay=delay;
		this.fromApp = fromApp;
		this.toApp = toApp;
		this.unprocessedData = unprocessedData;
	}

	/**
	 * The delayed event when manages the file tranfer.
	 */
	@Override
	protected void eventAction() {

		if (toApp.broker.vm.getState().equals(VirtualMachine.State.RUNNING)) {
			final long unprocessed = unprocessedData;
			try {
				
				NetworkNode.initTransfer(unprocessedData, ResourceConsumption.unlimitedProcessing,
						fromApp.computingAppliance.iaas.repositories.get(0), toApp.computingAppliance.iaas.repositories.get(0), new ConsumptionEvent() {

							@Override
							public void conComplete() {
								toApp.sumOfArrivedData += unprocessed;
								toApp.incomingData--;
							}

							@Override
							public void conCancelled(ResourceConsumption problematic) {
								System.err.println("WARNING: File transfer between applications is unsuccessful!");
				                System.exit(0); // TODO: it should not be an error.
							}

						});
			} catch (NetworkException e) {
				e.printStackTrace();
			} 
		}else {
			if(this.delay==1) {
				new BrokerCheck(this.fromApp,this.toApp,this.unprocessedData, 1);
			}else {
				new BrokerCheck(this.fromApp,this.toApp,this.unprocessedData, this.delay/2);
			}
		}
	}
	
}