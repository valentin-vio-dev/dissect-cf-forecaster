package hu.u_szeged.inf.fog.simulator.loaders;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement( name = "instances" )
@XmlAccessorType(XmlAccessType.PROPERTY) 
public class InstancesModel{
    ArrayList<InstanceModel> instanceList;

    public ArrayList<InstanceModel> getInstances(){
        return instanceList;
    }

    @XmlElement( name = "instance" )
    public void setInstances( ArrayList<InstanceModel> instances ){
        this.instanceList = instances;
    }

    public void add( InstanceModel instances ){
        if( this.instanceList == null )
        {
            this.instanceList = new ArrayList<InstanceModel>();
        }
        this.instanceList.add( instances );

    }

    @Override
    public String toString(){
        StringBuffer str = new StringBuffer();
        for( InstanceModel instance : this.instanceList )
        {	
        	str.append("\n");
            str.append( instance.toString() );
        }
        return str.toString();
    }

}