package hu.u_szeged.inf.fog.simulator.demo;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.AlterableResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;
import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.iot.*;
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

public class CercirasSim2 {

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
        String cloudfile = ScenarioBase.resourcePath + "/LPDS_32.xml";
        String fogfile = ScenarioBase.resourcePath + "/LPDS_16.xml";

        // Parameters
        double nodeRange = 500 * 1000; // 500 km
        long appFreq = 60 * 1000 * 5; // 30 min
        long taskSize = 262144; // 0.25 MB
        double countOfInst = 1200; // 5 min
        int threshold = 1;
        String fogStrategy1 = "load"; // hold, push, load
        String fogStrategy2 = "push";
        // Nodes
        ComputingAppliance fogLFStation = new ComputingAppliance(fogfile, "Liszt Ferenc station", new GeoLocation(47.436998252, 19.257165638), true, 100 * 1000);
        ComputingAppliance fogKalocsa = new ComputingAppliance(fogfile, "Kalocsa foktő station", new GeoLocation(46.5435333, 18.9430218), true, 100 * 1000);
        ComputingAppliance fogDebrecenIA = new ComputingAppliance(fogfile, "Debrecen International Airport", new GeoLocation(47.48666472, 21.60916423), true, 100 * 1000);
        ComputingAppliance fogKecskemetAirport = new ComputingAppliance(fogfile, "Kecskemet Airport", new GeoLocation(46.917162998, 19.742830362), true, 100 * 1000);


        ComputingAppliance cloudBudapest = new ComputingAppliance(cloudfile, "Budapest main", new GeoLocation(47.497913, 19.040236), true, nodeRange);

        // Applications
        Application fogapp1 = new Application(appFreq, taskSize, "instance2", "LF-Station-app", countOfInst, threshold, fogStrategy1, true);
        Application fogapp2 = new Application(appFreq, taskSize, "instance2", "Kalocsa-app", countOfInst, threshold, fogStrategy2, true);
        Application fogapp3 = new Application(appFreq, taskSize, "instance2", "Debrecent-app", countOfInst, threshold, fogStrategy1, true);
        Application fogapp4 = new Application(appFreq, taskSize, "instance2", "Kecskemet-app", countOfInst, threshold, fogStrategy2, true);

        Application cloudapp1 = new Application(appFreq, taskSize, "instance1", "Budapest-app", countOfInst, threshold, fogStrategy2, false);

        // Registration of the app modules
        fogLFStation.addApplication(fogapp1);
        fogKalocsa.addApplication(fogapp2);
        fogDebrecenIA.addApplication(fogapp3);
        fogKecskemetAirport.addApplication(fogapp4);
        cloudBudapest.addApplication(cloudapp1);

        // Connections and latencies
        fogDebrecenIA.setLatency(fogKalocsa, 7);
        fogLFStation.setLatency(fogKalocsa, 15);
        fogLFStation.setLatency(cloudBudapest, 3);
        fogDebrecenIA.setLatency(fogKecskemetAirport, 11);
        fogKecskemetAirport.setLatency(fogKalocsa, 12);
        fogKecskemetAirport.setLatency(fogKecskemetAirport, 9);

        fogDebrecenIA.addNeighbor(fogKalocsa, fogLFStation);
        fogKecskemetAirport.addNeighbor(fogLFStation);


        fogKalocsa.setParentNode(fogKecskemetAirport);
        fogDebrecenIA.setParentNode(fogKecskemetAirport);
        fogLFStation.setParentNode(cloudBudapest);
        fogKecskemetAirport.setParentNode(fogLFStation);

        // Devices
        HashMap< String, Integer > latencyMap = new HashMap < String, Integer > ();
        long disksize = 1073741824L;

        EnumMap< PowerTransitionGenerator.PowerStateKind, Map< String, PowerState>> transitions =
                MicrocontrollerPowerTransitionGenerator.generateTransitions(0.065, 1.475, 2.0, 0, 0);

        Map < String, PowerState > cpuTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.host);
        Map < String, PowerState > stTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.storage);
        Map < String, PowerState > nwTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.network);

        ArrayList<GeoLocation> list = new ArrayList<GeoLocation>();
        list.add(new GeoLocation(47.436998252, 19.257165638));
        list.add(new GeoLocation(46.5435333, 18.9430218));
        list.add(new GeoLocation(47.48666472, 21.60916423));
        list.add(new GeoLocation(46.917162998, 19.742830362));
        list.add(new GeoLocation(47.497913, 19.040236));

        for (int i = 0; i < 5; i++) {
            for (int j= 0; j < 30; j++) {
                MicroController mc = new MicroController(1, 0.001, 536870912L,
                        new Repository(disksize, "mc", 1562, 1562, 1562, latencyMap, stTransitions, nwTransitions),
                        1, 1, cpuTransitions);

                GeoLocation gl = list.get(i);
                if(j%3 == 0)
                    new Station(0, 10 * 24* 60 * 60 * 1000, 1024 * 64, 5, new CostDeviceStrategy(), 30 * 60 * 1000, gl , mc, 50, 1000, true, null, new Actuator(1));
                else
                    new Station(0, 10 * 24* 60 * 60 * 1000, 1024 * 5, 5, new RandomDeviceStrategy(), 30 * 60 * 1000, gl , mc, 50, 1000, true, null, new Actuator(1));
            }
        }


        // IoT pricing
        ArrayList <BluemixProvider.Bluemix> bmList = new ArrayList <BluemixProvider.Bluemix> ();
        bmList.add(new BluemixProvider.Bluemix(0, 499999, 0.00097));
        bmList.add(new BluemixProvider.Bluemix(450000, 6999999, 0.00068));
        bmList.add(new BluemixProvider.Bluemix(7000000, Long.MAX_VALUE, 0.00014));

        new BluemixProvider(bmList, fogapp1);
        new BluemixProvider(bmList, fogapp2);
        new BluemixProvider(bmList, fogapp3);
        new BluemixProvider(bmList, fogapp4);
        new BluemixProvider(bmList, cloudapp1);

        new AmazonProvider(5, 1000000, 512, fogapp1);
        new AmazonProvider(5, 1000000, 512, fogapp2);
        new AmazonProvider(5, 1000000, 512, fogapp3);
        new AmazonProvider(5, 1000000, 512, fogapp4);
        new AmazonProvider(5, 1000000, 512, cloudapp1);

        new AzureProvider(86400000, 421.65, 6000000, 4, fogapp1);
        new AzureProvider(86400000, 421.65, 6000000, 4, fogapp2);
        new AzureProvider(86400000, 421.65, 6000000, 4, fogapp3);
        new AzureProvider(86400000, 421.65, 6000000, 4, fogapp4);
        new AzureProvider(86400000, 421.65, 6000000, 4, cloudapp1);

        new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp1);
        new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp2);
        new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp3);
        new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp4);
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
