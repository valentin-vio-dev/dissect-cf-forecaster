package hu.u_szeged.inf.fog.simulator.demo;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.AlterableResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;
import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.datareader.SensorData;
import hu.u_szeged.inf.fog.simulator.datareader.SensorDataReader;
import hu.u_szeged.inf.fog.simulator.iot.RandomDeviceStrategy;
import hu.u_szeged.inf.fog.simulator.iot.StationRD;
import hu.u_szeged.inf.fog.simulator.iot.actuator.Actuator;
import hu.u_szeged.inf.fog.simulator.iot.mobility.GeoLocation;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;
import hu.u_szeged.inf.fog.simulator.physical.MicroController;
import hu.u_szeged.inf.fog.simulator.providers.*;
import hu.u_szeged.inf.fog.simulator.util.EnergyChartGenerator;
import hu.u_szeged.inf.fog.simulator.util.MicrocontrollerPowerTransitionGenerator;
import hu.u_szeged.inf.fog.simulator.util.TimelineGenerator;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class IoTSimulationWithTraceFile {
    public static void main(String[] args) throws Exception {

        String cloudfile = ScenarioBase.resourcePath+"LPDS_original.xml";

        ComputingAppliance cloud1 = new ComputingAppliance(cloudfile, "cloud1",new GeoLocation(0, 0), true, 0);
        ComputingAppliance cloud2 = new ComputingAppliance(cloudfile, "cloud2",new GeoLocation(0, 0), true, 0);

        VirtualAppliance va = new VirtualAppliance("va", 100, 0, false, 1073741824L);

          AlterableResourceConstraints arc1 = new AlterableResourceConstraints(1,0.001,4294967296L);
        AlterableResourceConstraints arc2 = new AlterableResourceConstraints(2,0.001,4294967296L);
        AlterableResourceConstraints arc3 = new AlterableResourceConstraints(4,0.001,4294967296L);
        AlterableResourceConstraints arc4 = new AlterableResourceConstraints(8,0.001,4294967296L);

        new Instance(va,arc1,0.00000001,"instance1");
        new Instance(va,arc2,0.000000015,"instance2");
        new Instance(va,arc3,0.000000020,"instance3");
        new Instance(va,arc4,0.000000025,"instance4");

        Application ca1 = new Application(5*60*1000, 256000, "instance1", "app1", 2400.0,1, "random", true);
        Application ca2 = new Application(5*60*1000, 256000, "instance2", "app2", 2400.0,1, "random", true);
        Application ca3 = new Application(5*60*1000, 256000, "instance3", "app3", 2400.0,1, "random", true);
        Application ca4 = new Application(5*60*1000, 256000, "instance4", "app4", 2400.0,1, "random", true);

        cloud1.addApplication(ca1,ca2);
        cloud2.addApplication(ca3,ca4);
       
        SensorDataReader reader = new SensorDataReader("src/main/resources/demo/feed.csv", SensorDataReader.CSV_COMMA_SEPARATOR, SensorDataReader.PATTERN_yyyyMMdd_HHmmss, false, 0);

        ArrayList<SensorData> dataList = reader.ReadAllLines();
        
        HashMap<String, Integer> latencyMap = new HashMap<String, Integer>();
    	final long disksize = 100001;
    	
    	final EnumMap<PowerTransitionGenerator.PowerStateKind, Map<String, PowerState>> transitions = 
    				MicrocontrollerPowerTransitionGenerator.generateTransitions(0.065, 1.475, 2.0, 0, 0);
    	
    	final Map<String, PowerState> cpuTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.host);
    	final Map<String, PowerState> stTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.storage);
    	final Map<String, PowerState> nwTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.network);
    	
        for(int i=0;i<1;i++) {
        	MicroController mc = new MicroController(1, 1, 1000,
   				 new Repository(disksize, "mc", 100, 100, 100, latencyMap, stTransitions, nwTransitions),
   				 1, 1, cpuTransitions);
        	 
   		new StationRD(0, 10*60*1000, dataList, new RandomDeviceStrategy(), new GeoLocation(0, 0), mc, 1000, true, new Actuator(1));

        }

        ArrayList<BluemixProvider.Bluemix> bmList = new ArrayList<BluemixProvider.Bluemix>();
        bmList.add(new BluemixProvider.Bluemix(0,499999,0.00097));
        bmList.add(new BluemixProvider.Bluemix(450000,6999999,0.00068));
        bmList.add(new BluemixProvider.Bluemix(7000000,Long.MAX_VALUE,0.00014));

        new BluemixProvider(bmList,ca1); new BluemixProvider(bmList,ca2);
        new BluemixProvider(bmList,ca3); new BluemixProvider(bmList,ca4);

        new AmazonProvider(5,1000000,512,ca1); new AmazonProvider(5,1000000,512,ca2);
        new AmazonProvider(5,1000000,512,ca4); new AmazonProvider(5,1000000,512,ca3);

        new AzureProvider(86400000,421.65,6000000,4,ca1); new AzureProvider(86400000,421.65,6000000,4,ca2);
        new AzureProvider(86400000,421.65,6000000,4,ca3); new AzureProvider(86400000,421.65,6000000,4,ca4);
     
        //TODO: Oracle Provider uses the (s).sensorCount, so it must set to 1 by default (?)
        new OracleProvider(2678400000L,0.93,15000,0.02344,1000, ca1); new OracleProvider(2678400000L,0.93,15000,0.02344,1000, ca2);
        new OracleProvider(2678400000L,0.93,15000,0.02344,1000, ca3); new OracleProvider(2678400000L,0.93,15000,0.02344,1000, ca4);
		
        long starttime = System.nanoTime();
        Timed.simulateUntilLastEvent();
        long stopttime = System.nanoTime();
       
		TimelineGenerator.generate(null);
	    EnergyChartGenerator.generateForDevices(null);
	    EnergyChartGenerator.generateForNodes(null);
    	ScenarioBase.printInformation(stopttime-starttime);
    }
}
