package hu.u_szeged.inf.fog.simulator.demo;

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
import hu.u_szeged.inf.fog.simulator.providers.*;
import hu.u_szeged.inf.fog.simulator.util.EnergyChartGenerator;
import hu.u_szeged.inf.fog.simulator.util.MicrocontrollerPowerTransitionGenerator;
import hu.u_szeged.inf.fog.simulator.util.TimelineGenerator;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class CercirasSim1 {


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
        long appFreq = 60 * 1000 * 5; // 5 min
        long taskSize = 262144; // 0.25 MB
        double countOfInst = 1200; // 5 min
        int threshold = 1;
        String fogStrategy1 = "random"; // hold, push, load
        String fogStrategy2 = "hold";
        // Nodes
        ComputingAppliance fogBerlin = new ComputingAppliance(fogfile, "Berlin", new GeoLocation(52.31, 13.23), true, nodeRange);
        ComputingAppliance fogHamburg= new ComputingAppliance(fogfile, "Hamburg", new GeoLocation(53.33, 10.0), true, nodeRange);
        ComputingAppliance fogCologne = new ComputingAppliance(fogfile, "Cologne", new GeoLocation(50.56, 6.57), true, nodeRange);

        ComputingAppliance fogMunich = new ComputingAppliance(fogfile, "Munich", new GeoLocation(48.8, 11.34), true, nodeRange);
        ComputingAppliance fogBremen = new ComputingAppliance(fogfile, "Bremen", new GeoLocation(53.5, 8.48), true, nodeRange);


        ComputingAppliance cloudFrankfurt = new ComputingAppliance(cloudfile, "Frankfurt", new GeoLocation(52, 8), true, nodeRange);

        // Applications
        Application fogapp1 = new Application(appFreq, taskSize, "instance2", "Berlin-app", countOfInst, threshold, fogStrategy1, true);
        Application fogapp3 = new Application(appFreq, taskSize, "instance2", "Hamburg-app", countOfInst, threshold, fogStrategy2, true);
        Application fogapp4 = new Application(appFreq, taskSize, "instance2", "Cologne-app", countOfInst, threshold, fogStrategy1, true);
        Application fogapp7 = new Application(appFreq, taskSize, "instance2", "Munich-app", countOfInst, threshold, fogStrategy2, true);
        Application fogapp8 = new Application(appFreq, taskSize, "instance2", "Bremen-app", countOfInst, threshold, fogStrategy1, true);

        Application cloudapp1 = new Application(appFreq, taskSize, "instance1", "Frankfurt-app", countOfInst, threshold, fogStrategy2, false);

        // Registration of the app modules
        fogBerlin.addApplication(fogapp1);
        fogHamburg.addApplication(fogapp3);
        fogCologne.addApplication(fogapp4);
        fogMunich.addApplication(fogapp7);
        fogBremen.addApplication(fogapp8);
        cloudFrankfurt.addApplication(cloudapp1);

        // Connections and latencies
        fogBerlin.setLatency(fogHamburg, 7);
        fogBerlin.setLatency(fogCologne, 9);
        fogBerlin.setLatency(fogMunich, 28);
        fogBerlin.setLatency(fogBremen, 27);
        fogHamburg.setLatency(fogMunich, 12);
        fogHamburg.setLatency(fogBremen, 16);
        fogHamburg.setLatency(cloudFrankfurt, 30);
        fogBremen.setLatency(fogCologne, 15);
        fogBremen.setLatency(fogMunich, 21);
        fogCologne.setLatency(fogMunich, 8);


        fogBerlin.addNeighbor(fogHamburg, fogCologne, fogMunich, fogBremen);


        fogMunich.setParentNode(fogBremen);
        fogBremen.setParentNode(fogCologne);
        fogHamburg.setParentNode(fogCologne);
        fogCologne.setParentNode(fogBerlin);
        fogBerlin.setParentNode(cloudFrankfurt);


        // Devices
        HashMap< String, Integer > latencyMap = new HashMap < String, Integer > ();
        long disksize = 1073741824L;

        EnumMap< PowerTransitionGenerator.PowerStateKind, Map< String, PowerState>> transitions =
                MicrocontrollerPowerTransitionGenerator.generateTransitions(0.065, 1.475, 2.0, 0, 0);

        Map < String, PowerState > cpuTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.host);
        Map < String, PowerState > stTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.storage);
        Map < String, PowerState > nwTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.network);

        ArrayList<GeoLocation> list = new ArrayList<GeoLocation>();
        list.add(new GeoLocation(52.31, 13.23)); //Berlin
        list.add(new GeoLocation(53.33, 10.0)); //Hamburg
        list.add(new GeoLocation(50.56, 6.57)); //Cologne
        list.add(new GeoLocation(48.8, 11.34)); //Munich
        list.add(new GeoLocation(53.5, 8.48)); //Bremen


        for (int i = 0; i < 5; i++) {
            for (int j= 0; j < 40; j++) {
                MicroController mc = new MicroController(1, 0.001, 536870912L,
                        new Repository(disksize, "mc", 1562, 1562, 1562, latencyMap, stTransitions, nwTransitions),
                        1, 1, cpuTransitions);

                GeoLocation gl = list.get(i);
                if(j%3==0){
                    new Station(0, 24* 60 * 60 * 1000, 50, 2, new DistanceDeviceStrategy() , 60 * 1000, gl , mc, 50, 1000, true, null, new Actuator(1));
                }
                else {
                    new Station(0, 24* 60 * 60 * 1000, 150, 2, new DistanceDeviceStrategy() , 60 * 1000, gl , mc, 50, 1000, true, new RandomMobilityStrategy(gl, 26.8224, 20 * 1000), new Actuator(1));
                }

            }
        }

        // IoT pricing
        ArrayList <BluemixProvider.Bluemix> bmList = new ArrayList <BluemixProvider.Bluemix> ();
        bmList.add(new BluemixProvider.Bluemix(0, 499999, 0.00097));
        bmList.add(new BluemixProvider.Bluemix(450000, 6999999, 0.00068));
        bmList.add(new BluemixProvider.Bluemix(7000000, Long.MAX_VALUE, 0.00014));

        new BluemixProvider(bmList, fogapp1);
        new BluemixProvider(bmList, fogapp3);
        new BluemixProvider(bmList, fogapp4);
        new BluemixProvider(bmList, fogapp7);
        new BluemixProvider(bmList, fogapp8);
        new BluemixProvider(bmList, cloudapp1);

        new AmazonProvider(5, 1000000, 512, fogapp1);
        new AmazonProvider(5, 1000000, 512, fogapp3);
        new AmazonProvider(5, 1000000, 512, fogapp4);
        new AmazonProvider(5, 1000000, 512, fogapp7);
        new AmazonProvider(5, 1000000, 512, fogapp8);
        new AmazonProvider(5, 1000000, 512, cloudapp1);

        new AzureProvider(86400000, 421.65, 6000000, 4, fogapp1);
        new AzureProvider(86400000, 421.65, 6000000, 4, fogapp3);
        new AzureProvider(86400000, 421.65, 6000000, 4, fogapp4);
        new AzureProvider(86400000, 421.65, 6000000, 4, fogapp7);
        new AzureProvider(86400000, 421.65, 6000000, 4, fogapp8);
        new AzureProvider(86400000, 421.65, 6000000, 4, cloudapp1);

        new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp1);
        new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp3);
        new OracleProvider(2678400000L, 0.93, 15000, 0.02344, 1000, fogapp4);
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
