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
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "device")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DeviceModel {

    public String name;
    public long startTime;
    public long stopTime;
    public long fileSize;
    public int sensorCount;
    public String strategy;
    public long freq;
    public double latitude;
    public double longitude;
    public double speed;
    public long radius;
    public int latency;
    
    public long capacity;
    public long maxInBW;
    public long maxOutBW;
    public long diskBW;
    public double cores;
    public double perCorePocessing;
	public long ram;
	public int onD;
	public int offD;
	public double minpower;
	public double idlepower;
	public double maxpower;

    @XmlAttribute(name = "name", required = true)
    public void setName(String name) {
		this.name = name;
	}

    @XmlElement(name = "startTime")
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

    @XmlElement(name = "stopTime")
    public void setStopTime(long stopTime) {
		this.stopTime = stopTime;
	}

    @XmlElement(name = "fileSize")
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

    @XmlElement(name = "sensorCount")
	public void setSensorCount(int sensorCount) {
		this.sensorCount = sensorCount;
	}

    @XmlElement(name = "strategy")
	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

    @XmlElement(name = "freq")
	public void setFreq(long freq) {
		this.freq = freq;
	}
    
    @XmlElement(name = "latitude")
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

    @XmlElement(name = "longitude")
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

    @XmlElement(name = "speed")
	public void setSpeed(double speed) {
		this.speed = speed;
	}

    @XmlElement(name = "radius")
	public void setRadius(long radius) {
		this.radius = radius;
	}

    @XmlElement(name = "latency")
	public void setLatency(int latency) {
		this.latency = latency;
	}
    
    @XmlElement(name = "capacity") 
	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

    @XmlElement(name = "maxInBW") 
	public void setMaxInBW(long maxInBW) {
		this.maxInBW = maxInBW;
	}

    @XmlElement(name = "maxOutBW") 
	public void setMaxOutBW(long maxOutBW) {
		this.maxOutBW = maxOutBW;
	}
    
    @XmlElement(name = "diskBW") 
	public void setDiskBW(long diskBW) {
		this.diskBW = diskBW;
	}

    @XmlElement(name = "cores") 
	public void setCores(double cores) {
		this.cores = cores;
	}

    @XmlElement(name = "perCorePocessing") 
	public void setPerCorePocessing(double perCorePocessing) {
		this.perCorePocessing = perCorePocessing;
	}

    @XmlElement(name = "ram") 
	public void setRam(long ram) {
		this.ram = ram;
	}

    @XmlElement(name = "onD") 
	public void setOnD(int onD) {
		this.onD = onD;
	}

    @XmlElement(name = "offD") 
	public void setOffD(int offD) {
		this.offD = offD;
	}

    @XmlElement(name = "minpower") 
	public void setMinpower(double minpower) {
		this.minpower = minpower;
	}

    @XmlElement(name = "idlepower") 
	public void setIdlepower(double idlepower) {
		this.idlepower = idlepower;
	}

    @XmlElement(name = "maxpower") 
	public void setMaxpower(double maxpower) {
		this.maxpower = maxpower;
	}
   
	@Override
	public String toString() {
		return "DeviceModel [name=" + name + ", startTime=" + startTime + ", stopTime=" + stopTime + ", fileSize="
				+ fileSize + ", sensorCount=" + sensorCount + ", strategy=" + strategy + ", freq=" + freq
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", speed=" + speed + ", radius=" + radius
				+ ", latency=" + latency + ", capacity=" + capacity + ", maxInBW=" + maxInBW + ", maxOutBW=" + maxOutBW
				+ ", diskBW=" + diskBW + ", cores=" + cores + ", perCorePocessing=" + perCorePocessing + ", ram=" + ram
				+ ", onD=" + onD + ", offD=" + offD + ", minpower=" + minpower + ", idlepower=" + idlepower
				+ ", maxpower=" + maxpower + "]";
	}

	public static ArrayList < DeviceModel > loadDeviceXML(String stationfile) throws JAXBException {
        File file = new File(stationfile);
        JAXBContext jaxbContext = JAXBContext.newInstance(DevicesModel.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        DevicesModel device = (DevicesModel) jaxbUnmarshaller.unmarshal(file);
        //@ VIO_REMOVED_COMMENT @ System.out.println(device.deviceList);
        return device.deviceList;

    }
}