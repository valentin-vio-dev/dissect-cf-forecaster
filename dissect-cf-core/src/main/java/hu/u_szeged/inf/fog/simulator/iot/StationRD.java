package hu.u_szeged.inf.fog.simulator.iot;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.u_szeged.inf.fog.simulator.datareader.SensorData;
import hu.u_szeged.inf.fog.simulator.iot.actuator.Actuator;
import hu.u_szeged.inf.fog.simulator.iot.mobility.GeoLocation;
import hu.u_szeged.inf.fog.simulator.physical.MicroController;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import java.util.ArrayList;

/**
 * This class is able to model the behavior of an IoT device based real IoT trace file. 
 * The position of the device is fixed.
 */
public class StationRD extends Device {

    /**
     * Constructs an IoT device, which reads sensor data from trace file. 
     * Even if it reads the sensor measurements from a file, 
     * the lifetime of the station still depends on the start time and the stop time.
     * It directly chooses the IoT application which the device can communicate with.
     * The count of sensors is set to 1 by default.
     * @param startTime When the IoT device starts working (in milliseconds).
     * @param stopTime When the IoT device stops working (in milliseconds).
     * @param dataList The trace file which was created by the SensorDataReader class.
     * @param strategy The policy to determine which IoT application the current IoT device communicates with.
     * @param x The x position of the device in a coordinate system.
     * @param y The y position of the device in a coordinate system.
     * @param mc The physical properties of the device, i.e. CPU, memory, network.
     * @param sensorFreq The length of one measurement of a sensor (in milliseconds). 
     * @param readEnergy Tells if the energy measuring is on or off.
     */
    public StationRD(long startTime, long stopTime, ArrayList < SensorData > dataList, DeviceStrategy strategy, GeoLocation geoLocation,
        MicroController mc, long sensorFreq, boolean readEnergy, Actuator actuator) {
        this.stopTime = stopTime;
        this.dataList = dataList;
        this.startTime = startTime;
        this.deviceStrategy = strategy;
        this.mc = mc;
        this.sensorFreq = sensorFreq;
        this.readEnergy = readEnergy;
        this.geoLocation = geoLocation;
        this.actuator = actuator;
        this.actuator.device = this;
        this.deviceStrategy.d = this;
        this.tickCounter = 1;
        this.startMeter();
        Device.allDevices.add(this);
        this.nextData = dataList.get(0);
        this.sensorCount = 1;
    }

    /**
     * The number of current row of the trace file under processing.
     */
    public int tickCounter;

    /**
     * The actual row, which was transformed to (simulated) data.
     */
    public SensorData currentData;

    /**
     * All rows in the trace file.
     */
    public ArrayList < SensorData > dataList;

    /**
     * The next row in the trace file.
     */
    public SensorData nextData;

    /**
     * The recurring event of the device handles the data generating and sending process.
     */
    @Override
    public void tick(long fires) {
        if (Timed.getFireCount() < stopTime && Timed.getFireCount() >= startTime) {
            if (tickCounter < dataList.size()) {
                try {
                    this.mc.setStateToMetering(); // TODO: calculate the length of the metering state.
                    currentData = nextData;
                    saveToLocalRepo(currentData);
                    nextData = dataList.get(tickCounter);
                    freq = nextData.date - currentData.date;
                    updateFrequency(freq);
                    this.mc.setStateToRunning();
                } catch (NetworkException e) {
                    e.printStackTrace();
                }
            } else if (tickCounter == dataList.size()) {
                try {
                    this.mc.setStateToMetering();
                    currentData = nextData;
                    saveToLocalRepo(currentData);
                    this.mc.setStateToRunning();
                } catch (NetworkException e) {
                    e.printStackTrace();
                }
            }
            tickCounter++;
        }
        this.deviceStrategy.update();
        if (this.mc.localDisk.getFreeStorageCapacity() == this.mc.localDisk.getMaxStorageCapacity() && Timed.getFireCount() > stopTime) {
            this.stopMeter();
        }

        try {
            if (this.nodeRepository.currState.equals(Repository.State.RUNNING)) {
                this.startCommunicate();
            }
        } catch (NetworkException e) {
            e.printStackTrace();
        }

        if (!this.app.isSubscribed()) {
            try {
                this.app.restartApplication();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The method which tries to save the data read from trace file.
     * @param sd The actual row transformed to simulated data.
     */
    private void saveToLocalRepo(SensorData sd) {
        if (this.mc.localDisk.registerObject(sd)) {
            this.sumOfGeneratedData += sd.size;
            this.messageCount += 1;
        } else {
            try {
                System.err.println("WARNING: Saving data into the local repository is unsuccessful!");
                System.exit(0); // TODO: it should not be an error.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}