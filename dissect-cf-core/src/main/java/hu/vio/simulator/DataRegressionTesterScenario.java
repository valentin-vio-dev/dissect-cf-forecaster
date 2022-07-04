package hu.vio.simulator;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.AlterableResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;
import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.iot.CostDeviceStrategy;
import hu.u_szeged.inf.fog.simulator.iot.RandomDeviceStrategy;
import hu.u_szeged.inf.fog.simulator.iot.Station;
import hu.u_szeged.inf.fog.simulator.iot.actuator.Actuator;
import hu.u_szeged.inf.fog.simulator.iot.mobility.GeoLocation;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;
import hu.u_szeged.inf.fog.simulator.physical.MicroController;
import hu.u_szeged.inf.fog.simulator.providers.*;
import hu.u_szeged.inf.fog.simulator.util.MicrocontrollerPowerTransitionGenerator;
import hu.vio.simulator.predictor.ArimaPredictor;
import hu.vio.simulator.predictor.Predictor;
import hu.vio.simulator.predictor.ProphetPredictor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class DataRegressionTesterScenario {

    public static ComputeNodeManager computeNodeManager = new ComputeNodeManager(false);
    public static Predictor predictor = new Predictor(new ArimaPredictor(), 256, 20);

    public static void main(String[] args) throws Exception {
        Utils.cleanAndCreateDirectory(Utils.getRoot() + "/nodes/tmp");

        Config config = new Config()
            .put("nodeRange_cloud", 500 * 1000)
            .put("nodeRange_fog", 100 * 1000)

            .put("appFreq", 60 * 1000 * 5)
            .put("taskSize", 262144 * 100)
            .put("countOfInst", 12000)
            .put("threshold", 1)
            .put("va_startupProcess", 100)
            .put("va_req_disk", 1073741824L)

            .put("fogStrategy1", "load")
            .put("fogStrategy2", "push")

            .put("arc1_cpu", 8)
            .put("arc2_cpu", 4)

            .put("arc1_processing", 0.001 / 100)
            .put("arc2_processing", 0.001 / 100)

            .put("arc1_memory", 4294967296L)
            .put("arc2_memory", 4294967296L)

            .put("disksize", 1073741824L)

            .put("chunkSize", predictor.getChunkSize())
            .put("smooth", predictor.getSmooth())
            .put("predictor", predictor.getName());

        config.print();
        config.saveConfig(Utils.getRoot() + "/nodes/tmp/config.xml");


        // Resource files
        String cloudfile = Utils.getRoot() + "/dissect-cf-application/src/main/resources/demo/XML_examples/LPDS_vio.xml";
        String fogfile = Utils.getRoot() + "/dissect-cf-application/src/main/resources/demo/XML_examples/LPDS_vio.xml";


        // 1 GB VM image
        VirtualAppliance va = new VirtualAppliance("va", config.get("va_startupProcess", Integer.class), 0, false, config.get("va_req_disk", Long.class));


        // flavors with 4-8 CPU cores, 4 GB memory
        AlterableResourceConstraints arc1 = new AlterableResourceConstraints(
                config.get("arc1_cpu", Integer.class),
                config.get("arc1_processing", Double.class),
                config.get("arc1_memory", Long.class)
        ); // 5min

        AlterableResourceConstraints arc2 = new AlterableResourceConstraints(
                config.get("arc2_cpu", Integer.class),
                config.get("arc2_processing", Double.class),
                config.get("arc2_memory", Long.class)
        ); // 10min


        // Instances
        new Instance(va, arc1, 0.0000000566666667, "instance1");
        new Instance(va, arc2, 0.0000000283333333, "instance2");


        // Nodes
        ComputingAppliance fogLFStation = new ComputingAppliance(
            fogfile,
            "Liszt Ferenc station",
            new GeoLocation(47.436998252, 19.257165638),
            true,
            config.get("nodeRange_fog", Integer.class)
        );

        ComputingAppliance fogKalocsa = new ComputingAppliance(
            fogfile,
            "Kalocsa foktő station",
            new GeoLocation(46.5435333, 18.9430218),
            true,
            config.get("nodeRange_fog", Integer.class)
        );

        ComputingAppliance fogDebrecenIA = new ComputingAppliance(
            fogfile,
            "Debrecen International Airport",
            new GeoLocation(47.48666472, 21.60916423),
            true,
            config.get("nodeRange_fog", Integer.class)
        );

        ComputingAppliance fogKecskemetAirport = new ComputingAppliance(
            fogfile,
            "Kecskemet Airport",
            new GeoLocation(46.917162998, 19.742830362),
            true,
            config.get("nodeRange_fog", Integer.class)
        );

        ComputingAppliance cloudBudapest = new ComputingAppliance(
            cloudfile,
            "Budapest main",
            new GeoLocation(47.497913, 19.040236),
            true,
            config.get("nodeRange_cloud", Integer.class)
        );

        computeNodeManager.addComputingAppliances(fogLFStation, fogKalocsa, fogDebrecenIA, fogKecskemetAirport, cloudBudapest);


        // Applications
        Application fogapp1 = new Application(
            config.get("appFreq", Integer.class),
            config.get("taskSize", Integer.class),
            "instance2",
            "LF-Station-app",
            config.get("countOfInst", Integer.class),
            config.get("threshold", Integer.class),
            config.get("fogStrategy1", String.class),
            true
        );

        Application fogapp2 = new Application(
            config.get("appFreq", Integer.class),
            config.get("taskSize", Integer.class),
            "instance2",
            "Kalocsa-app",
            config.get("countOfInst", Integer.class),
            config.get("threshold", Integer.class),
            config.get("fogStrategy2", String.class),
            true
        );

        Application fogapp3 = new Application(
            config.get("appFreq", Integer.class),
            config.get("taskSize", Integer.class),
            "instance2",
            "Debrecent-app",
            config.get("countOfInst", Integer.class),
            config.get("threshold", Integer.class),
            config.get("fogStrategy1", String.class),
            true
        );

        Application fogapp4 = new Application(
            config.get("appFreq", Integer.class),
            config.get("taskSize", Integer.class),
            "instance2",
            "Kecskemet-app",
            config.get("countOfInst", Integer.class),
            config.get("threshold", Integer.class),
            config.get("fogStrategy2", String.class),
            true
        );

        Application cloudapp1 = new Application(
            config.get("appFreq", Integer.class),
            config.get("taskSize", Integer.class),
            "instance1",
            "Budapest-app",
            config.get("countOfInst", Integer.class),
            config.get("threshold", Integer.class),
            config.get("fogStrategy2", String.class),
            false
        );

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
        HashMap<String, Integer> latencyMap = new HashMap<> ();

        EnumMap<PowerTransitionGenerator.PowerStateKind, Map<String, PowerState>> transitions =
                MicrocontrollerPowerTransitionGenerator.generateTransitions(0.065, 1.475, 2.0, 0, 0);

        Map <String, PowerState> cpuTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.host);
        Map <String, PowerState> stTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.storage);
        Map <String, PowerState> nwTransitions = transitions.get(PowerTransitionGenerator.PowerStateKind.network);

        ArrayList<GeoLocation> list = new ArrayList<>();
        list.add(new GeoLocation(47.436998252, 19.257165638));
        list.add(new GeoLocation(46.5435333, 18.9430218));
        list.add(new GeoLocation(47.48666472, 21.60916423));
        list.add(new GeoLocation(46.917162998, 19.742830362));
        list.add(new GeoLocation(47.497913, 19.040236));

        for (int i = 0; i < 5; i++) {
            for (int j= 0; j < 30; j++) {
                MicroController mc = new MicroController(1, 0.001, 536870912L,
                        new Repository(config.get("disksize", Long.class), "mc", 1562, 1562, 1562, latencyMap, stTransitions, nwTransitions),
                        1, 1, cpuTransitions);

                GeoLocation gl = list.get(i);
                if(j % 3 == 0)
                    new Station(0, 10 * 24* 60 * 60 * 1000, 1024 * 64, 5, new CostDeviceStrategy(), 30 * 60 * 1000, gl , mc, 50, 1000, true, null, new Actuator(1));
                else
                    new Station(0, 10 * 24* 60 * 60 * 1000, 1024 * 5, 5, new RandomDeviceStrategy(), 30 * 60 * 1000, gl , mc, 50, 1000, true, null, new Actuator(1));
            }
        }


        // IoT pricing
        ArrayList <BluemixProvider.Bluemix> bmList = new ArrayList <> ();
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

        Timed.simulateUntilLastEvent();

        computeNodeManager.exportAll(Utils.getRoot() + "/nodes/tmp");
    }
}
