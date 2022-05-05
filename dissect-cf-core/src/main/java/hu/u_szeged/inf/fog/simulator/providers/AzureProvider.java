package hu.u_szeged.inf.fog.simulator.providers;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.iot.Device;

/**
 * It represents the Azure IoT provider which calculates its cost based a monthly price,
 * but it has a restriction for message sizes and the total messages allowed per day.
 */
public class AzureProvider extends Provider{

	/**
	 * Helper variable to calculate the daily message limit.
	 */
	private long usedMessage;

	/**
	 * It prints the actual cost of the provider.
	 */
	@Override
	public String toString() {
		return "[AZURE=" + this.cost + "]";
	}

	/**
	 * It helps to create the Azure provider without XML file.
	 * @param azureFreq The frequency of the provider, it should be a day in milliseconds.
	 * @param pricePerMonth The monthly price.
	 * @param messagesPerDay The message limit per day.
	 * @param messagesizePerKB The message size limit.
	 * @param app The application which is monitored by this provider.
	 */
	public AzureProvider(long azureFreq,double pricePerMonth,long messagesPerDay,long messagesizePerKB,Application app) {
		super(app);
		this.azureFreq=azureFreq;
		this.pricePerMonth=pricePerMonth;
		this.messagesPerDay=messagesPerDay;
		this.messagesizePerKB=messagesizePerKB;
		this.usedMessage=0;
		this.startProvider();
	}

	/**
	 * This constructor should be used only when initialization is done from XML file.
	 * @param app The application which is monitored by this provider.
	 */
	public AzureProvider(Application app) {
		super();
		this.usedMessage=0;
		this.app=app;
	}
	
	/**
	 * This method calculates the average file size if devices generate files with different size.
	 */
	private long avarageFileSize() {
		long tmp=0;
		for(Device s : this.app.deviceList) {
			tmp+=s.fileSize;
		}
		if(this.app.deviceList.size()==0) {
			return 0;
		}
		return tmp/this.app.deviceList.size();
	}
	
	/**
	 * This method calculates the cost based on the frequency of the class.
	 */
	public void tick(long fires) {
		if(this.app.isSubscribed()==false) {
			unsubscribe();
		}
		
		if(this.messagesPerDay>0 && this.avarageFileSize()<=(this.messagesizePerKB*1024)){
			if(this.avarageFileSize()==0) {
				this.cost=-1;
			}else {
				long totalMessages=this.app.sumOfProcessedData / this.avarageFileSize();
				long msg = totalMessages - usedMessage;
				usedMessage= msg;
				
				if(msg<=this.messagesPerDay){
					long month = Timed.getFireCount()/this.getFrequency();
					if(month==0){
						month=1;
						this.cost=this.pricePerMonth*month;
					}else if(Timed.getFireCount()%(this.getFrequency())!=0){
						this.cost=this.pricePerMonth*(month+1);
					}else{
						this.cost=this.pricePerMonth*month;
					}
				}else{
					this.cost=-1;
				}
			}
			
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
		subscribe(this.azureFreq);
	}
}
