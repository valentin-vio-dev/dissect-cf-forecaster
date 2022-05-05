package hu.u_szeged.inf.fog.simulator.demo;

import java.io.File;
import java.util.concurrent.TimeUnit;
import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.application.Application.VmCollector;
import hu.u_szeged.inf.fog.simulator.iot.Device;
import hu.u_szeged.inf.fog.simulator.iot.actuator.Actuator;
import hu.u_szeged.inf.fog.simulator.iot.actuator.ChangeNodeActuatorEvent;
import hu.u_szeged.inf.fog.simulator.iot.actuator.ChangePositionActuatorEvent;
import hu.u_szeged.inf.fog.simulator.iot.actuator.ConnectToNodeActuatorEvent;
import hu.u_szeged.inf.fog.simulator.iot.actuator.DisconnectFromNodeActuatorEvent;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

/**
 * Helper class to manage and visualize the simulation (logging).
 */
public abstract class ScenarioBase {

	/** 
	 * Path to the resource files.
	 */
	protected final static String resourcePath = new StringBuilder(System.getProperty("user.dir")).
			append(File.separator).
      		append(File.separator).
			append("src").
			append(File.separator).
			append("main").
			append(File.separator).
			append("resources").
			append(File.separator).
			append("demo").
			append(File.separator).
			toString();

  public static void printInformation(long runtime) {
    //@ VIO_REMOVED_COMMENT @ System.out.println("~~Information about the simulation:~~");
    
    double totalCost = 0;
    long generatedData = 0, processedData = 0, arrivedData = 0;
    int usedVM = 0;
    int tasks = 0;
    double totalEnergyConsumptionOfNodes = 0.0;
    double totalEnergyConsumptionOfDevices = 0.0;
    long highestDeviceStopTime = Long.MIN_VALUE;
    long highestApplicationStopTime = Long.MIN_VALUE;
    long makespan = Long.MIN_VALUE;
    double bluemix = 0;
    double amazon = 0;
    double azure = 0;
    double oracle = 0;
    
    for (ComputingAppliance c: ComputingAppliance.allComputingAppliance) {
      //@ VIO_REMOVED_COMMENT @ System.out.println();
      //@ VIO_REMOVED_COMMENT @ System.out.println("Computing Appliance: " + c.name + " energy consumption: " + c.energyConsumption);
      
      totalEnergyConsumptionOfNodes += c.energyConsumption;
      
      for (Application a: c.applicationList) {
        //@ VIO_REMOVED_COMMENT @ System.out.println("Application: " + a.name);
        
        totalCost += a.instance.calculateCloudCost(a.sumOfWorkTime);
        processedData += a.sumOfProcessedData;
        arrivedData += a.sumOfArrivedData;
        usedVM += a.vmCollectorList.size();

        for (VmCollector vmcl: a.vmCollectorList) {
          tasks += vmcl.taskCounter;
          //@ VIO_REMOVED_COMMENT @ System.out.println("VM-" + vmcl.id + " tasks: " + vmcl.taskCounter + " worktime: " + vmcl.workTime + " installed at: " +
          //@ VIO_REMOVED_COMMENT @   vmcl.installed + " restarted: " + vmcl.restarted);
        }

        for (Device d: a.deviceList) {
          generatedData += d.sumOfGeneratedData;
          totalEnergyConsumptionOfDevices += d.energyConsumption;

          if (d.stopTime > highestDeviceStopTime) {
            highestDeviceStopTime = d.stopTime;
          }
        }

        if (a.stopTime > highestApplicationStopTime) {
          highestApplicationStopTime = a.stopTime;
        }

        //@ VIO_REMOVED_COMMENT @ System.out.println(a.name + " devices: " + a.deviceList.size() + " node cost:" + a.instance.calculateCloudCost(a.sumOfWorkTime));
        
        if(!a.providers.isEmpty()) {

            //@ VIO_REMOVED_COMMENT @ System.out.println(a.providers);
            bluemix += a.providers.get(0).cost;
            amazon += a.providers.get(1).cost;
            azure += a.providers.get(2).cost;
            oracle += a.providers.get(3).cost;
        }

      }

      //@ VIO_REMOVED_COMMENT @ System.out.println();
    }
    
    makespan = highestApplicationStopTime - highestDeviceStopTime;

    //@ VIO_REMOVED_COMMENT @ System.out.println("VMs (pc.): " + usedVM + " tasks (pc.): " + tasks);
    //@ VIO_REMOVED_COMMENT @ System.out.println("Node cost (Euro): " + totalCost);
    //@ VIO_REMOVED_COMMENT @ System.out.println("IoT cost (Euro) -  Bluemix: " + bluemix + " Amazon: " + amazon + " Azure: " + azure + " Oracle: " + oracle);
    //@ VIO_REMOVED_COMMENT @ System.out.println("Nodes energy (W): " + totalEnergyConsumptionOfNodes + " Devices energy (W): " + totalEnergyConsumptionOfDevices);
    //@ VIO_REMOVED_COMMENT @ System.out.println("Network (seconds): " + TimeUnit.SECONDS.convert(Application.sumOfTimeOnNetwork, TimeUnit.MILLISECONDS));
    //@ VIO_REMOVED_COMMENT @ System.out.println("Network (MB): " + (Application.sumOfByteOnNetwork / 1024 / 1024));
    //@ VIO_REMOVED_COMMENT @ System.out.println("Timeout (minutes): " + ((double) makespan / 1000 / 60));
    //@ VIO_REMOVED_COMMENT @ System.out.println("App STOP (hours): " + ((double) highestApplicationStopTime / 1000 / 60 / 60) + " Device STOP (hours):" + ((double) highestDeviceStopTime / 1000 / 60 / 60));
    //@ VIO_REMOVED_COMMENT @ System.out.println("Generated / processed / arrived data (bytes): " + generatedData + "/" + processedData + "/" + arrivedData + " (~" + (arrivedData / 1024 / 1024) + " MB)");
    //@ VIO_REMOVED_COMMENT @ System.out.println("Runtime (seconds): " + TimeUnit.SECONDS.convert(runtime, TimeUnit.NANOSECONDS));
    //@ VIO_REMOVED_COMMENT @ System.out.println("Number of actuator events: " + Actuator.actuatorEventCounter );
	//@ VIO_REMOVED_COMMENT @ System.out.println("Number of actuator evens:\n" +
	//@ VIO_REMOVED_COMMENT @ 		"\tChangeNode: " + ChangeNodeActuatorEvent.changeNodeEventCounter +
	//@ VIO_REMOVED_COMMENT @ 		"\n\tChangePosition: " + ChangePositionActuatorEvent.changePositionEventCounter +
	//@ VIO_REMOVED_COMMENT @ 		"\n\tConnectToNode: " + ConnectToNodeActuatorEvent.connectToNodeEventCounter +
	//@ VIO_REMOVED_COMMENT @ 		"\n\tDisconnectFromNode: " + DisconnectFromNodeActuatorEvent.DisconnectFromNodeEventCounter );
	}
}