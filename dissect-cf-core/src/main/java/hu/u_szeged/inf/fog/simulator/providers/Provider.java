package hu.u_szeged.inf.fog.simulator.providers;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.loaders.ProvidersModel;
import hu.u_szeged.inf.fog.simulator.providers.BluemixProvider.Bluemix;

/**
 * This class is for creating IoT providers which calculate the utilization cost from the aspects
 * of cloud providers. Currently four IoT calculation schemes are implemented 
 * according to this: https://doi.org/10.1145/3053600.3053601 
 */
public abstract class Provider extends Timed{
		
	/**
	 * The path of the XML file containing the four provider schemes.
	 */
	public static String providerFile;
	
	/**
	 * A list of intervals of the transmitted data size. It used for Bluemix.
	 */
	public ArrayList<Bluemix> bmList;
	
	/**
	 * The application which applies the current provider.
	 */
	public Application app;
	
	/**
	 * A flag which initiates the unsubscribe process.
	 */
	public boolean needsToStop;
	
	/**
	 * The size of a block of data. Typically used for Amazon.
	 */
	public long blockSize;
	
	/**
	 * The number of messages delivered. Typically used for Amazon.  
	 */
	public long messageCount;
	
	/**
	 * The price for one block of data. Typically used for Amazon.
	 */
	public double blockPrice;

	/**
	 *  The price of connecting one device for a month. Typically used for Oracle.
	 */
	public double devicePricePerMonth;
	
	/**
	 * The number of messages permitted per month per device. Typically used for Oracle.
	 */
	public long messagesPerMonthPerDevice;
	
	/**
	 * Additional device price in case of exceeded limit. Typically used for Oracle.
	 */
	public double aDevicePricePerMonth;
	
	/**
	 * Additional number of thousand messages in case of exceeded limit. Typically used for Oracle.
	 */
	public long aMessagesPerMonthPerDevice;
	
	/**
	 * The frequency of calculation for the Oracle provider, typically it is set up to a month.
	 */
	public long oracleFreq;
	
	/**
	 * Monthly price of the Azure provider.
	 */
	public double pricePerMonth;
			 
	/**
	 * The maximum number of messages allowed in a day. Typically used for Azure.
	 */
	public long messagesPerDay;

	/**
	 * The maximum size of the messages allowed in a day. Typically used for Azure.
	 */
	public long messagesizePerKB;
	
	/**
	 * The frequency of calculation for the Azure provider, typically it is set up to a day.
	 * Typically used for Azure.
	 */
	public long azureFreq;
	
	/**
	 * The actual cost of the provider.
	 */
	public double cost;
		
	/**
	 * It generates the four providers for all IoT applications.
	 * @param providerfile The path of the provider file.
	 */
	public static void loadProvidersXML(String sourcefile){
		Provider.providerFile=sourcefile;
		for(Application app: Application.allApplication) {
			app.providers.add(new BluemixProvider(app));
			app.providers.add(new AmazonProvider(app));
			app.providers.add(new OracleProvider(app));
			app.providers.add(new AzureProvider(app));
		}
	}
	
	/**
	 * It connects the current IoT application with the actual IoT provider.
	 * @param app The application which is monitored by the provider.
	 */
	Provider(Application app){
		this.app=app;
		this.app.providers.add(this);
	}
	
	/**
	 * Default constructor supports the provider creation from XML file.
	 * It also starts the provider to monitor the application.
	 */
	Provider(){
		bmList = new ArrayList<Bluemix>();
		try {
			ProvidersModel.loadProvidersXML(Provider.providerFile, this);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		this.startProvider();
	}

	/**
	 * The method starts the actual provider. Initial costs can be calculated if needed. It needs to be overridden.
	 */
	public abstract void startProvider();
	
	/**
	 * The steps of the actual cost calculation should be define in this function. It needs to be overridden.
	 */
	@Override
	public void tick(long fires) {
	}

}
