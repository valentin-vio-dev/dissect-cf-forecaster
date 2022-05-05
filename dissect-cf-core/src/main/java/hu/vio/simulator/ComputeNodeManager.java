package hu.vio.simulator;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

import java.io.PrintWriter;

import java.util.*;

/**
 * Class that manages compute nodes data.
 */
public class ComputeNodeManager {

    /**
     * For logging...
     */
    private final boolean verbose;

    /**
     * Compute node data storage.
     */
    private final List<ComputeNodeData> computeNodeDataList;

    /**
     * List for Computing Appliances.
     */
    private final List<ComputingAppliance> computingAppliances;

    /**
     * Separate list for Computing Appliance names.
     */
    private final Set<String> computingApplianceNames;

    /**
     * A set for store all Physical Machine ids.
     */
    private final Set<Integer> ids;

    /**
     * Timer.
     */
    private final ComputeNodeTimer computeNodeTimer;

    public ComputeNodeManager(boolean verbose) {
        this.verbose = verbose;
        this.computeNodeDataList = new ArrayList<>();
        this.computingAppliances = new ArrayList<>();
        this.computingApplianceNames = new HashSet<>();
        this.ids = new HashSet<>();
        this.computeNodeTimer = new ComputeNodeTimer(1, this::extractAndAddFromAll);

        this.addFeatures();
    }

    /**
     * Function for initialize feature functions.
     */
    private void addFeatures() {
        ComputeNodeData.addFeature("Name", false, (computingAppliance, machine) -> computingAppliance.name);

        ComputeNodeData.addFeature("Load of resource", true, (computingAppliance, machine) -> {
            double result = 0.0;
            for (VirtualMachine vm: machine.listVMs()) {
                if (vm.getResourceAllocation() != null) {
                    result += vm.getResourceAllocation().allocated.getRequiredCPUs();
                }
            }
            //double running = computingAppliance.iaas.getRunningCapacities().getRequiredCPUs();
            return result;
        });

        ComputeNodeData.addFeature("Memory", true, (computingAppliance, machine) -> {
            double result = 0.0;
            for (VirtualMachine vm: machine.listVMs()) {
                if (vm.getResourceAllocation() != null) {
                    result += vm.getResourceAllocation().allocated.getRequiredMemory();
                }
            }
            //double running = computingAppliance.iaas.getRunningCapacities().getRequiredMemory();
            return result;
        });

        ComputeNodeData.addFeature("Total proc. power", true, (computingAppliance, machine) -> {
            double result = 0.0;
            for (VirtualMachine vm: machine.listVMs()) {
                if (vm.getResourceAllocation() != null) {
                    result += vm.getResourceAllocation().allocated.getRequiredProcessingPower();
                }
            }
            //double running = computingAppliance.iaas.getRunningCapacities().getRequiredProcessingPower();
            return result;
        });

        ComputeNodeData.addFeature("Tester", true, (computingAppliance, machine) -> {
            double usedCPU = 0.0;
            for (VirtualMachine vm: computingAppliance.iaas.listVMs()) {
                if (vm.getResourceAllocation() != null) {
                    usedCPU += vm.getResourceAllocation().allocated.getRequiredCPUs();
                }
            }
            double requiredCPUs = computingAppliance.iaas.getRunningCapacities().getRequiredCPUs();
            return requiredCPUs > 0 ? usedCPU / requiredCPUs * 100 : 0;
        });
    }

    /**
     * Function for add Computing Appliances.
     */
    public void addComputingAppliances(ComputingAppliance... computingAppliances) {
        Collections.addAll(this.computingAppliances, computingAppliances);
    }

    /**
     * Adds one compute node data to data the list.
     */
    public void add(ComputeNodeData data) {
        /*if (this.computeNodeDataList.size() > 25000) {
            exportAll(Utils.getRoot() + "/nodes/tmp");
            System.exit(1225);
            return;
        }*/

        if (verbose) {
            data.print();
        }

        ids.add(data.getMachine().id);
        computingApplianceNames.add(data.getComputingAppliance().name);
        computeNodeDataList.add(data);
    }

    /**
     * Extracts and adds all feature from all computing appliance.
     */
    public void extractAndAddFromAll() {
        for(ComputingAppliance computingAppliance: computingAppliances) {
            for(PhysicalMachine machine: computingAppliance.iaas.machines) {
                add(new ComputeNodeData(computingAppliance, machine));
            }
        }
    }

    /**
     * Exports all data to the given directory into separate .csv files.
     */
    public void exportAll(String path) {
        for(String caName: computingApplianceNames) {
            for(Integer id: ids) {
                List<ComputeNodeData> dataList = getAllNodeByCAAndPMId(caName, id);

                if (!dataList.isEmpty()) {
                    Logger.log("CA: " + caName, "PM: " + id, "LEN: " + dataList.size() + "\t\t" + path + "\\CA_" + caName + "_PM_" + id + ".csv");

                    try (PrintWriter printWriter = new PrintWriter(path + "\\CA_" + caName + "_PM_" + id + ".csv")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(ComputeNodeData.getColumns()).append("\n");
                        for (ComputeNodeData data: dataList) {
                            sb.append(data).append("\n");
                        }

                        printWriter.write(sb.toString());
                    } catch (Exception exception) {
                        System.err.println(exception.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Returns all compute node data by computing appliance and machine id.
     */
    private List<ComputeNodeData> getAllNodeByCAAndPMId(String caName, int id) {
        List<ComputeNodeData> result = new ArrayList<>();
        for(ComputeNodeData data: computeNodeDataList) {
            if (caName.equals(data.getComputingAppliance().name) && id == data.getMachine().id) {
                result.add(data);
            }
        }
        return result;
    }

    /**
     * Handler for timer.
     */
    public void tick() {
        this.computeNodeTimer.tick();
    }
}
