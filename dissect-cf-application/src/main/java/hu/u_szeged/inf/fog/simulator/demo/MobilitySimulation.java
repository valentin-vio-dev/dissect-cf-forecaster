package hu.u_szeged.inf.fog.simulator.demo;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.AlterableResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;
import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.iot.DistanceDeviceStrategy;
import hu.u_szeged.inf.fog.simulator.iot.Station;
import hu.u_szeged.inf.fog.simulator.iot.actuator.Actuator;
import hu.u_szeged.inf.fog.simulator.iot.mobility.GeoLocation;
import hu.u_szeged.inf.fog.simulator.iot.mobility.RandomMobilityStrategy;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;
import hu.u_szeged.inf.fog.simulator.physical.MicroController;
import hu.u_szeged.inf.fog.simulator.providers.AmazonProvider;
import hu.u_szeged.inf.fog.simulator.providers.AzureProvider;
import hu.u_szeged.inf.fog.simulator.providers.BluemixProvider;
import hu.u_szeged.inf.fog.simulator.providers.BluemixProvider.Bluemix;
import hu.u_szeged.inf.fog.simulator.providers.Instance;
import hu.u_szeged.inf.fog.simulator.providers.OracleProvider;
import hu.u_szeged.inf.fog.simulator.util.EnergyChartGenerator;
import hu.u_szeged.inf.fog.simulator.util.MicrocontrollerPowerTransitionGenerator;
import hu.u_szeged.inf.fog.simulator.util.TimelineGenerator;

public class MobilitySimulation {

   public static void main(String[] args) throws Exception {

      // 1 GB VM image
      VirtualAppliance va = new VirtualAppliance("va", 100, 0, false, 1073741824L);

      // flavors with 4-8 CPU cores, 4 GB memory
      AlterableResourceConstraints arc1 = new AlterableResourceConstraints(8, 0.001, 4294967296L); // 5min
      AlterableResourceConstraints arc2 = new AlterableResourceConstraints(4, 0.001, 4294967296L); // 10min

      // Instances
      new Instance(va, arc1, 0.0000000566666667, "instance1");
      new Instance(va, arc2, 0.0000000283333333, "instance2");

      // Resource files
      String cloudfile = ScenarioBase.resourcePath + "/XML_examples/LPDS_32.xml"; // 32 cores
      String fogfile = ScenarioBase.resourcePath + "/XML_examples/LPDS_16.xml"; // 16 cores

      // Parameters
      double nodeRange = 500 * 1000; // 500 km
      long appFreq = 1 * 60 * 1000; // 1 min
      long taskSize = 262144; // 0.25 MB
      double countOfInst = 1200; // 5 min
      int threshold = 1;
      String appStrategy = "load"; // hold, push, load
      
      // Nodes
      ComputingAppliance fogLondon = new ComputingAppliance(fogfile, "London", new GeoLocation(51, 0), true, nodeRange);
      ComputingAppliance fogAmsterdam = new ComputingAppliance(fogfile, "Amsterdam", new GeoLocation(52, 5), true, nodeRange);
      ComputingAppliance fogParis = new ComputingAppliance(fogfile, "Paris", new GeoLocation(49, 2), true, nodeRange);
      
      ComputingAppliance fogPrague = new ComputingAppliance(fogfile, "Prague", new GeoLocation(50, 14), true, nodeRange);
      ComputingAppliance fogVienna = new ComputingAppliance(fogfile, "Vienna", new GeoLocation(48, 16), true, nodeRange);
      ComputingAppliance fogBudapest = new ComputingAppliance(fogfile, "Budapest", new GeoLocation(47, 19), true, nodeRange);
      
      ComputingAppliance fogBrussels = new ComputingAppliance(cloudfile, "Brussels", new GeoLocation(50, 4), true, nodeRange);
      ComputingAppliance fogBratislava = new ComputingAppliance(cloudfile, "Bratislava", new GeoLocation(48, 17), true, nodeRange);
      
      ComputingAppliance cloudFrankfurt = new ComputingAppliance(cloudfile, "Frankfurt", new GeoLocation(52, 8), true, nodeRange);
      
      // Applications
      Application fogapp1 = new Application(appFreq, taskSize, "instance2", "London-app", countOfInst, threshold, appStrategy, true);
      Application fogapp3 = new Application(appFreq, taskSize, "instance2", "Amsterdam-app", countOfInst, threshold, appStrategy, true);
      Application fogapp4 = new Application(appFreq, taskSize, "instance2", "Paris-app", countOfInst, threshold, appStrategy, true);
      Application fogapp7 = new Application(appFreq, taskSize, "instance2", "Prague-app", countOfInst, threshold, appStrategy, true);
      Application fogapp8 = new Application(appFreq, taskSize, "instance2", "Vienna-app", countOfInst, threshold, appStrategy, true);
      Application fogapp6 = new Application(appFreq, taskSize, "instance2", "Budapest-app", countOfInst, threshold, appStrategy, true);
      
      Application fogapp5 = new Application(appFreq, taskSize, "instance1", "Bratislava-app", countOfInst, threshold, appStrategy, false);
      Application fogapp2 = new Application(appFreq, taskSize, "instance1", "Brussels-app", countOfInst, threshold, appStrategy, false);
      Application cloudapp1 = new Application(appFreq, taskSize, "instance1", "Frankfurt-app", countOfInst, threshold, appStrategy, false);

      // Registration of the app modules
      fogLondon.addApplication(fogapp1);
      fogBrussels.addApplication(fogapp2);
      fogAmsterdam.addApplication(fogapp3);
      fogParis.addApplication(fogapp4);
      fogBratislava.addApplication(fogapp5);
      fogBudapest.addApplication(fogapp6);
      fogPrague.addApplication(fogapp7);
      fogVienna.addApplication(fogapp8);
      cloudFrankfurt.addApplication(cloudapp1);

      // Connections and latencies
      fogLondon.setLatency(fogAmsterdam, 7);
      fogLondon.setLatency(fogParis, 9);
      fogLondon.setLatency(fogPrague, 28);
      fogLondon.setLatency(fogVienna, 27);
      fogLondon.setLatency(fogBudapest, 30);

      fogBratislava.setLatency(fogPrague, 1);
      fogBratislava.setLatency(fogVienna, 31);
      fogBratislava.setLatency(fogBudapest, 8);

      fogBrussels.setLatency(fogAmsterdam, 6);
      fogBrussels.setLatency(fogParis, 16);
      fogBrussels.setLatency(fogLondon, 17);

      fogBrussels.setLatency(fogBratislava, 23);

      cloudFrankfurt.setLatency(fogBratislava, 14);
      cloudFrankfurt.setLatency(fogBrussels, 12);
      
      fogLondon.addNeighbor(fogAmsterdam, fogParis, fogPrague, fogVienna, fogBudapest);

      fogBrussels.addNeighbor(fogBratislava);

      fogPrague.setParentNode(fogBratislava);
      fogVienna.setParentNode(fogBratislava);
      fogBudapest.setParentNode(fogBratislava);
      fogAmsterdam.setParentNode(fogBrussels);
      fogParis.setParentNode(fogBrussels);
      fogLondon.setParentNode(fogBrussels);

      fogBrussels.setParentNode(cloudFrankfurt);
      fogBratislava.setParentNode(cloudFrankfurt);

      // Devices
      
      long disksize = 1073741824L;

      ArrayList<GeoLocation> list = new ArrayList<GeoLocation>();
      list.add(new GeoLocation(52, 5));
      list.add(new GeoLocation(47, 19));
      list.add(new GeoLocation(48, 17));
      list.add(new GeoLocation(50, 4));
      list.add(new GeoLocation(52, 8));
      
      for (int i = 0; i < 5; i++) {
    	  for (int j= 0; j < 100; j++) {
    		  HashMap < String, Integer > latencyMap = new HashMap < String, Integer > ();
    		  EnumMap < PowerTransitionGenerator.PowerStateKind, Map < String, PowerState >> transitions =
    			         MicrocontrollerPowerTransitionGenerator.generateTransitions(0.065, 1.475, 2.0, 0, 0);

    			      Map < String, PowerState > cpuTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.host);
    			      Map < String, PowerState > stTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.storage);
    			      Map < String, PowerState > nwTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.network);
    			      
    		  MicroController mc = new MicroController(1, 0.001, 536870912L,
    		            new Repository(disksize, "mc", 1562, 1562, 1562, latencyMap, stTransitions, nwTransitions),
    		            1, 1, cpuTransitions);

    		      	 GeoLocation gl = list.get(i);
    		      	 new Station(0, 6* 60 * 60 * 1000, 50, 2, new DistanceDeviceStrategy() , 60 * 1000, gl , mc, 50, 1000, true, new RandomMobilityStrategy(gl, 26.8224, 500*1000), new Actuator(1));
    		      	 //new Station(0, 6* 60 * 60 * 1000, 50, 2, new RandomDeviceStrategy() , 60 * 1000, gl , mc, 50, 100, 1000, true, new RandomMobilityStrategy(gl, 26.8224, 500*1000), new Actuator(1));
    		      	 //new Station(0, 6* 60 * 60 * 1000, 50, 2, new RuntimeDeviceStrategy() , 60 * 1000, gl , mc, 50, 100, 1000, true, new RandomMobilityStrategy(gl, 26.8224, 500*1000), new Actuator(1));
    		    	 
    	  }
     }

      // IoT pricing
      ArrayList < Bluemix > bmList = new ArrayList < Bluemix > ();
      bmList.add(new Bluemix(0, 499999, 0.00097));
      bmList.add(new Bluemix(450000, 6999999, 0.00068));
      bmList.add(new Bluemix(7000000, Long.MAX_VALUE, 0.00014));

      new BluemixProvider(bmList, fogapp1);
      new BluemixProvider(bmList, fogapp2);
      new BluemixProvider(bmList, fogapp3);
      new BluemixProvider(bmList, fogapp4);
      new BluemixProvider(bmList, fogapp5);
      new BluemixProvider(bmList, fogapp6);
      new BluemixProvider(bmList, fogapp7);
      new BluemixProvider(bmList, fogapp8);
      new BluemixProvider(bmList, cloudapp1);

      new AmazonProvider(5, 1000000, 512, fogapp1);
      new AmazonProvider(5, 1000000, 512, fogapp2);
      new AmazonProvider(5, 1000000, 512, fogapp3);
      new AmazonProvider(5, 1000000, 512, fogapp4);
      new AmazonProvider(5, 1000000, 512, fogapp5);
      new AmazonProvider(5, 1000000, 512, fogapp6);
      new AmazonProvider(5, 1000000, 512, fogapp7);
      new AmazonProvider(5, 1000000, 512, fogapp8);
      new AmazonProvider(5, 1000000, 512, cloudapp1);

      new AzureProvider(86400000, 421.65, 6000000, 4, fogapp1);
      new AzureProvider(86400000, 421.65, 6000000, 4, fogapp2);
      new AzureProvider(86400000, 421.65, 6000000, 4, fogapp3);
      new AzureProvider(86400000, 421.65, 6000000, 4, fogapp4);
      new AzureProvider(86400000, 421.65, 6000000, 4, fogapp5);
      new AzureProvider(86400000, 421.65, 6000000, 4, fogapp6);
      new AzureProvider(86400000, 421.65, 6000000, 4, fogapp7);
      new AzureProvider(86400000, 421.65, 6000000, 4, fogapp8);
      new AzureProvider(86400000, 421.65, 6000000, 4, cloudapp1);

      new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp1);
      new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp2);
      new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp3);
      new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp4);
      new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp5);
      new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp6);
      new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp7);
      new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp8);
      new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, cloudapp1);

      // starting the simulation
      long starttime = System.nanoTime();
      Timed.simulateUntilLastEvent();
      long stopttime = System.nanoTime();

      // log
	  TimelineGenerator.generate(null);
      EnergyChartGenerator.generateForDevices(null);
	  EnergyChartGenerator.generateForNodes(null);
      ScenarioBase.printInformation(stopttime - starttime);
   }
}