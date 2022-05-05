package hu.u_szeged.inf.fog.simulator.loaders;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import hu.u_szeged.inf.fog.simulator.providers.BluemixProvider.Bluemix;
import hu.u_szeged.inf.fog.simulator.providers.Provider;

public class ProvidersModel{
	
	public static void loadProvidersXML(String providerfile,Provider p)throws ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File(providerfile);
		NodeList nList;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();

		//amazon
		nList = doc.getElementsByTagName("price");
		p.blockPrice=(Long.parseLong(nList.item(0).getTextContent()));
		nList = doc.getElementsByTagName("message-count");
		p.messageCount=(Long.parseLong(nList.item(0).getTextContent()));
		nList = doc.getElementsByTagName("block-size");
		p.blockSize=(Long.parseLong(nList.item(0).getTextContent()));
		
		nList = doc.getElementsByTagName("oracle");
		p.oracleFreq=(Long.parseLong(nList.item(0).getAttributes().item(0).getNodeValue()));
		nList = doc.getElementsByTagName("messages-per-month-per-device");
		p.messagesPerMonthPerDevice=(Long.parseLong(nList.item(0).getTextContent()));
		nList = doc.getElementsByTagName("deviceprice-per-month");
		p.devicePricePerMonth=(Double.parseDouble(nList.item(0).getTextContent()));
		nList = doc.getElementsByTagName("am-messages-per-month-per-device");
		p.aMessagesPerMonthPerDevice=(Long.parseLong(nList.item(0).getTextContent()));
		nList = doc.getElementsByTagName("am-deviceprice-per-month");
		p.aDevicePricePerMonth=(Double.parseDouble(nList.item(0).getTextContent()));

		//azure
		nList = doc.getElementsByTagName("azure");
		p.azureFreq=(Long.parseLong(nList.item(0).getAttributes().item(0).getNodeValue()));
		nList = doc.getElementsByTagName("price-per-month");
		p.pricePerMonth=(Double.parseDouble(nList.item(0).getTextContent()));
		nList = doc.getElementsByTagName("messages-per-day");
		p.messagesPerDay=(Long.parseLong(nList.item(0).getTextContent()));
		nList = doc.getElementsByTagName("messagesize-per-KB");
		p.messagesizePerKB=(Long.parseLong(nList.item(0).getTextContent()));
	

		nList = doc.getElementsByTagName("bluemix");
			nList = doc.getElementsByTagName("price-per-MB");

			double price, mbfrom, mbto;
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				mbfrom = Double.parseDouble(nNode.getAttributes().item(0).getTextContent());
				mbto = Double.parseDouble(nNode.getAttributes().item(1).getTextContent());
				price = Double.parseDouble(nNode.getTextContent());
				
				if (mbto == -1) {
					mbto = Double.MAX_VALUE;
				}
				p.bmList.add(new Bluemix(mbfrom, mbto, price));
			
		}

	}
}