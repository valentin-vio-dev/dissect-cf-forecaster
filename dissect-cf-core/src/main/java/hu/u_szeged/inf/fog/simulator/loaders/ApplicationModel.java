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


@XmlRootElement(name = "application")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ApplicationModel {

    public String name;
    public long tasksize;
    public long freq;
    public String instance;
    public double countOfInstructions;
    public int threshold;
    public String strategy;
    public boolean canJoin;

    @XmlAttribute(name = "name", required = true)
    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "tasksize")
    public void setTasksize(long tasksize) {
        this.tasksize = tasksize;
    }

    @XmlElement(name = "instance")
    public void setInstance(String instance) {
        this.instance = instance;
    }

    @XmlElement(name = "freq")
    public void setFreq(long freq) {
        this.freq = freq;
    }

    @XmlElement(name = "countOfInstructions")
    public void setCountOfInstructions(double countOfInstructions) {
        this.countOfInstructions = countOfInstructions;
    }

    @XmlElement(name = "threshold")
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @XmlElement(name = "strategy")
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @XmlElement(name = "canJoin")
    public void setCanJoin(boolean canJoin) {
        this.canJoin = canJoin;
    }

    @Override
	public String toString() {
		return "ApplicationModel [name=" + name + ", tasksize=" + tasksize + ", freq=" + freq + ", instance=" + instance
				+ ", countOfInstructions=" + countOfInstructions + ", threshold=" + threshold + ", strategy=" + strategy
				+ ", canJoin=" + canJoin + "]";
	}

	public static ArrayList < ApplicationModel > loadApplicationXML(String appfile) throws JAXBException {
        File file = new File(appfile);
        JAXBContext jaxbContext = JAXBContext.newInstance(ApplicationsModel.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ApplicationsModel app = (ApplicationsModel) jaxbUnmarshaller.unmarshal(file);

        return app.applicationList;
    }

}