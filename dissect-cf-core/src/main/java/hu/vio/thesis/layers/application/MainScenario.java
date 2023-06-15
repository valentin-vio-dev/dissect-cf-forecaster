package hu.vio.thesis.layers.application;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
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
import hu.vio.thesis.layers.application.refactored.MessageProtocol;
import hu.vio.thesis.layers.application.refactored.SocketClient;
import hu.vio.thesis.layers.application.refactored.predictors.Predictor;
import hu.vio.thesis.layers.application.refactored.Feature;
import hu.vio.thesis.layers.application.refactored.FeatureManager;

import java.util.*;

public class MainScenario {

    public static void main(String[] args) throws Exception {
        Args.setArgs(args); // Needs to be right after the main entry point.
        /*
         * Example args: predictor="arima" chunkSize="256" trainSize="0.75" smoothing="20" hyperParameters="{'arima-p_value': 2, 'arima-d_value': 0, 'arima-q_value': 0, 'arima-alpha': 0.05}" host="127.0.0.1" port="65432" outputLocation="D:/dev/test" saveDataset="False" saveStandalone="False" saveDatasetImage="False" variableConfig="{'node_range_cloud': 500000, 'node_range_fog': 10000, 'app_freq': 300000, 'task_size': 26214400, 'count_of_inst': 12000, 'threshold': 1, 'va_startup_process': 100, 'va_req_disk': 1073741824, 'fog_strategy_1': 'load', 'fog_strategy_2': 'push', 'arc1_cpu': 8, 'arc2_cpu': 4, 'arc1_processing': 1e-05, 'arc2_processing': 1e-05, 'arc1_memory': 4294967296, 'arc2_memory': 4294967296, 'disk_size': 1073741824}" saveConfig="False" pred_id="0"
         */

        Config config = new Config(Args.get("variableConfig"));
        config.print();

        SocketClient socketClient = new SocketClient(
                Args.get("host", "127.0.0.1"),
                Integer.parseInt(Args.get("port", "65432"))
        );

        try {
            socketClient.startConnection();
            socketClient.send(new MessageProtocol(MessageProtocol.Command.LOG, "socket-started", "Connection has been established!"));
            Logger.log("Connection has been established!");
        } catch (Exception e) {
            Logger.log("Connection has not been established!");
        }

        Predictor predictor = Predictor.getInstance();
        predictor.setSocketClient(socketClient);

        if (Boolean.parseBoolean(Args.get("saveConfig", "true"))) {
            config.saveConfigKV(Args.get("outputLocation") + "/config/config.cfg");
        }

        // Resource files
        String cloudFilePath = "D:/dev/dissect-cf/dissect-cf-application/src/main/resources/demo/XML_examples/LPDS_vio.xml";
        String fogFilePath = "D:/dev/dissect-cf/dissect-cf-application/src/main/resources/demo/XML_examples/LPDS_vio.xml";

        // 1 GB VM image
        VirtualAppliance va = new VirtualAppliance(
                "va",
                config.getInteger("va_startup_process"),
                0,
                false,
                config.getLong("va_req_disk")
        );


        // flavors with 4-8 CPU cores, 4 GB memory
        AlterableResourceConstraints arc1 = new AlterableResourceConstraints(
                config.getInteger("arc1_cpu"),
                config.getDouble("arc1_processing"),
                config.getLong("arc1_memory")
        ); // 5min

        AlterableResourceConstraints arc2 = new AlterableResourceConstraints(
                config.getInteger("arc2_cpu"),
                config.getDouble("arc2_processing"),
                config.getLong("arc2_memory")
        ); // 10min


        // Instances
        new Instance(va, arc1, 0.0000000566666667, "instance1");
        new Instance(va, arc2, 0.0000000283333333, "instance2");


        // Nodes
        ComputingAppliance fogLFStation = new ComputingAppliance(
                fogFilePath,
                "Liszt Ferenc station",
                new GeoLocation(47.436998252, 19.257165638),
                true,
                config.getInteger("node_range_fog")
        );

        ComputingAppliance fogKalocsa = new ComputingAppliance(
                fogFilePath,
                "Kalocsa foktő station",
                new GeoLocation(46.5435333, 18.9430218),
                true,
                config.getInteger("node_range_fog")
        );

        ComputingAppliance fogDebrecenIA = new ComputingAppliance(
                fogFilePath,
                "Debrecen International Airport",
                new GeoLocation(47.48666472, 21.60916423),
                true,
                config.getInteger("node_range_fog")
        );

        ComputingAppliance fogKecskemetAirport = new ComputingAppliance(
                fogFilePath,
                "Kecskemet Airport",
                new GeoLocation(46.917162998, 19.742830362),
                true,
                config.getInteger("node_range_fog")
        );

        ComputingAppliance cloudBudapest = new ComputingAppliance(
                cloudFilePath,
                "Budapest main",
                new GeoLocation(47.497913, 19.040236),
                true,
                config.getInteger("node_range_cloud")
        );


        // Applications
        Application fogapp1 = new Application(
                config.getInteger("app_freq"),
                config.getInteger("task_size"),
                "instance2",
                "LF-Station-app",
                config.getInteger("count_of_inst"),
                config.getInteger("threshold"),
                config.getString("fog_strategy_1"),
                true
        );

        Application fogapp2 = new Application(
                config.getInteger("app_freq"),
                config.getInteger("task_size"),
                "instance2",
                "Kalocsa-app",
                config.getInteger("count_of_inst"),
                config.getInteger("threshold"),
                config.getString("fog_strategy_2"),
                true
        );

        Application fogapp3 = new Application(
                config.getInteger("app_freq"),
                config.getInteger("task_size"),
                "instance2",
                "Debrecent-app",
                config.getInteger("count_of_inst"),
                config.getInteger("threshold"),
                config.getString("fog_strategy_1"),
                true
        );

        Application fogapp4 = new Application(
                config.getInteger("app_freq"),
                config.getInteger("task_size"),
                "instance2",
                "Kecskemet-app",
                config.getInteger("count_of_inst"),
                config.getInteger("threshold"),
                config.getString("fog_strategy_2"),
                true
        );

        Application cloudapp1 = new Application(
                config.getInteger("app_freq"),
                config.getInteger("task_size"),
                "instance1",
                "Budapest-app",
                config.getInteger("count_of_inst"),
                config.getInteger("threshold"),
                config.getString("fog_strategy_2"),
                false
        );

        // Registration of the app modules
        fogLFStation.addApplication(fogapp1);
        fogKalocsa.addApplication(fogapp2);
        fogDebrecenIA.addApplication(fogapp3);
        fogKecskemetAirport.addApplication(fogapp4);
        cloudBudapest.addApplication(cloudapp1);

        // Set the features for prediction
        FeatureManager featureManager = FeatureManager.getInstance();
        predictor.setFeatureManager(featureManager);

        for (ComputingAppliance computingAppliance: ComputingAppliance.allComputingAppliance) {
            featureManager.addFeature(new Feature(String.format("%s__%s", computingAppliance.name, "Memory"), true) {
                @Override
                public double compute() {
                    double result = 0.0;
                    for (PhysicalMachine physicalMachine: computingAppliance.iaas.machines) {
                        for (VirtualMachine vm: physicalMachine.listVMs()) {
                            if (vm.getResourceAllocation() != null) {
                                result += vm.getResourceAllocation().allocated.getRequiredMemory();
                            }
                        }
                    }
                    return result;
                }
            });

            featureManager.addFeature(new Feature(String.format("%s__%s", computingAppliance.name, "Load of resource"), true) {
                @Override
                public double compute() {
                    double result = 0.0;
                    for (PhysicalMachine physicalMachine: computingAppliance.iaas.machines) {
                        for (VirtualMachine vm: physicalMachine.listVMs()) {
                            if (vm.getResourceAllocation() != null) {
                                result += vm.getResourceAllocation().allocated.getRequiredCPUs();
                            }
                        }
                    }
                    return result;
                }
            });

            featureManager.addFeature(new Feature(String.format("%s__%s", computingAppliance.name, "Total processing power"), true) {
                @Override
                public double compute() {
                    double result = 0.0;
                    for (PhysicalMachine physicalMachine: computingAppliance.iaas.machines) {
                        for (VirtualMachine vm: physicalMachine.listVMs()) {
                            if (vm.getResourceAllocation() != null) {
                                result += vm.getResourceAllocation().allocated.getRequiredProcessingPower();
                            }
                        }
                    }
                    return result;
                }
            });
        }

        // Connections and latencies
        fogKecskemetAirport.setLatency(fogDebrecenIA, 4);
        fogLFStation.setLatency(fogDebrecenIA, 8);
        fogDebrecenIA.setLatency(cloudBudapest, 7);
        fogKalocsa.setLatency(cloudBudapest, 3);

        fogDebrecenIA.addNeighbor(fogKalocsa);
        fogKecskemetAirport.addNeighbor(fogLFStation);

        fogLFStation.setParentNode(fogDebrecenIA);
        fogKecskemetAirport.setParentNode(fogDebrecenIA);
        fogKalocsa.setParentNode(cloudBudapest);
        fogDebrecenIA.setParentNode(cloudBudapest);


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
                        new Repository(config.getLong("disk_size"), "mc", 1562, 1562, 1562, latencyMap, stTransitions, nwTransitions),
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

        if (Boolean.parseBoolean(Args.get("saveDataset", "true"))) {
            featureManager.exportToCSV(Args.get("outputLocation") + "/dataset/dataset.csv");
        }

        socketClient.stopConnection();
    }
}