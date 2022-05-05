package hu.u_szeged.inf.fog.simulator.providers;

import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.iot.Device;

/**
 * This class represents the Oracle IoT provider which has a restriction on how many messages
 *  can a device deliver per month. If the number of messages sent by a device is greater than 
 *  the device’s category permits, an additional device price will be charged with a predefined amount of messages.
 */
public class OracleProvider extends Provider {

	/**
	 * It prints the actual cost of the provider.
	 */
	@Override
	public String toString() {
		return "[ORACLE=" + cost+"]";
	}

	/**
	 * This constructor should be used only when initialization is done from XML file.
	 * @param app The application which is monitored by this provider.
	 */
	public OracleProvider(Application app) {
		super();
		this.app=app;
	}
	
	/**
	 * It helps to create Oracle provider without XML file.
	 * @param oracleFreq The frequency of the provider, it should be a month in milliseconds.
	 * @param devicepricePerMonth The device price per month.
	 * @param messagesPerMonthPerDevice The allowed number of messages for one device.
	 * @param amDevicepricePerMonth Additional device price.
	 * @param amMessagesPerMonthPerDevice Additional number of messages for one device.
	 * @param app The application which is monitored by this provider.
	 */
	public OracleProvider(long oracleFreq,double devicepricePerMonth,long messagesPerMonthPerDevice,
	double amDevicepricePerMonth,long amMessagesPerMonthPerDevice,Application app) {
		super(app);
		this.devicePricePerMonth=devicepricePerMonth;
		this.messagesPerMonthPerDevice=messagesPerMonthPerDevice;
		this.aDevicePricePerMonth=amDevicepricePerMonth;
		this.aMessagesPerMonthPerDevice=amMessagesPerMonthPerDevice;
		this.oracleFreq=oracleFreq;
		this.startProvider();
	}
	
	/**
	 * This method calculates the costs based on the frequency of the class.
	 */
	public void tick(long fires) {
		if(this.aMessagesPerMonthPerDevice>0){
				for(Device s : this.app.deviceList){
					long month = s.stopTime/(this.getFrequency());
					if(month==0){
						month=1;
						this.cost=this.cost+this.devicePricePerMonth*(s).sensorCount*month;
					}else if(s.stopTime%(this.getFrequency())!=0){
						this.cost=this.cost+this.devicePricePerMonth*(s).sensorCount*(month+1);
					}else{
						this.cost=this.cost+this.devicePricePerMonth*(s).sensorCount*month;
					}
					// additional cost
					long device = s.messageCount/(s).sensorCount;// 1 device hany uzenetet generalt
					s.messageCount = 0; 
					if(device>this.messagesPerMonthPerDevice){
						device-=this.messagesPerMonthPerDevice;
						long whole=device/this.aMessagesPerMonthPerDevice;
						if(whole==0){
							this.cost+=this.aDevicePricePerMonth;
						}else if((device%this.aMessagesPerMonthPerDevice)!=0){
							this.cost+=this.aDevicePricePerMonth*(whole+1);
						}else{
							this.cost+=this.aDevicePricePerMonth*(whole);
						}
					} 
				}
			if(this.needsToStop) {
				unsubscribe();
			}
			
		}
	}

	/**
	 * This method starts the work of the provider with the given frequency.
	 */
	@Override
	public void startProvider() {
		subscribe(oracleFreq);
		this.needsToStop=false;
	}
}
