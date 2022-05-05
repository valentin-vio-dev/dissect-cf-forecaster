package hu.u_szeged.inf.fog.simulator.providers;

import hu.u_szeged.inf.fog.simulator.application.Application;

/**
 * This class represents the Amazon IoT provider. The price is based on the delivery cost
 * (the number of messages delivered by AWS IoT to devices or applications) and 
 * a message is a 512-byte block of data. 
 */
public class AmazonProvider extends Provider{
	
	/**
	 * It prints the actual cost of the provider.
	 */
	@Override
	public String toString() {
		return "[AMAZON=" + cost +"]";
	}


	/**
	 * This constructor should be used only when initialization is done from XML file.
	 * @param app The application which is monitored by this provider.
	 */
	public AmazonProvider(Application app) {
		super();
		this.app=app;
	}
	
	/**
	 * It helps to create the Amazon providerwithout XML file.
	 * @param blockPrice Cost of one block.
	 * @param messageCount It tells how many messages belongs to one unit.
	 * @param blockSize Amount of data which belongs to one block.
	 * @param app The application which is monitored by this provider.
	 */
	public AmazonProvider(double blockPrice, long messageCount, long blockSize,Application app) {
		super(app);
		this.blockPrice=blockPrice;
		this.messageCount=messageCount;
		this.blockSize=blockSize;
		this.startProvider();
	}
	
	/**
	 * This method calculates the costs based on the frequency of the class.
	 */
	public void tick(long fires) {		
		if(this.blockPrice>0 && this.blockSize>0){
			this.cost= (((double)this.app.sumOfProcessedData / this.blockSize) + 1) * this.blockPrice / this.messageCount;
		}
		if(this.needsToStop) {
			unsubscribe();
		}
	}

	/**
	 * This method starts the work of the provider with the given frequency.
	 */
	@Override
	public void startProvider() {
		subscribe(Integer.MAX_VALUE);
		
	}
}
