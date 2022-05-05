package hu.u_szeged.inf.fog.simulator.loaders;

import java.io.File;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "appliance")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ApplianceModel {
	
	public String name;
	public double latitude;
	public double longitude;
	public double range;
	public String file;
	
	public ArrayList<ApplicationModel> applications;
	public ArrayList<NeigboursModel> neighbours; 

	@XmlAttribute( name = "name", required = true )
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "latitude" )
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	@XmlElement(name = "longitude" )
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}	

	@XmlElement(name = "range" )
	public void setRange(double range) {
		this.range = range;
	}	
	
	@XmlElement(name = "file")
	public void setFile(String file) {
		this.file = file;
	}
	
	public ArrayList<ApplicationModel> getApplications(){
		return applications;
	}
		
	@Override
	public String toString() {
		return "ApplianceModel [name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + ", range="
				+ range + ", file=" + file + ", applications=" + applications + ", neighbours=" + neighbours + "]";
	}

	@XmlElementWrapper( name = "applications" )
	@XmlElement( name = "application")
	public void setApplications(ArrayList<ApplicationModel> applications) {
		this.applications = applications;
	}
	
	public void add( ApplicationModel applicationModel) {
		if ( this.applications == null) {
			this.applications = new ArrayList<ApplicationModel>();
		}
		this.applications.add(applicationModel);
	}
	
	public ArrayList<NeigboursModel> getNeighbourAppliances(){
		return neighbours;
	}
	
	@XmlElementWrapper( name = "neighbours")
	@XmlElement( name = "neighbour")
	public void setNeighbourAppliances(ArrayList<NeigboursModel> neighbours) {
		this.neighbours = neighbours;
	}
	
	public void add ( NeigboursModel device) {
		if (this.neighbours == null) {
			this.neighbours = new ArrayList<NeigboursModel>();
		}
		this.neighbours.add(device);
	}
	
	public static ArrayList<ApplianceModel> loadAppliancesXML(String appliancefile) throws JAXBException {
		File file = new File(appliancefile);
		JAXBContext jaxbContext = JAXBContext.newInstance( AppliancesModel.class );
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		AppliancesModel appliances = (AppliancesModel) jaxbUnmarshaller.unmarshal( file );
		for(int i=0; i<appliances.applianceList.size();i++) {
			//@ VIO_REMOVED_COMMENT @ System.out.println(appliances.applianceList.get(i));
		}
		return appliances.applianceList;
	}
	
}
