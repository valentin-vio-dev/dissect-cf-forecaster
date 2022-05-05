package hu.u_szeged.inf.fog.simulator.datareader;

import hu.mta.sztaki.lpds.cloud.simulator.io.StorageObject;

import java.util.Date;

/**
 * This class represent one row from a trace file. It extends the StorageObject class with date stored in UNIX
 * Objects of this class are ready to be used in a simulation.
 */
public class SensorData extends StorageObject {
	
	/**
	 * The moment when the object was measured in the real life.
	 */
    public final long date;

    /**
     * It creates an object from a real world measurement.
     * @param date The moment when the data was measured in the real life.
     * @param id Identifier of the measurement.
     * @param size Size of the measurement.
     */
    public SensorData(long date, String id, long size)  {
        super(id, size, false);
        this.date = date;
    }

    /**
     * ToString method, it is helpful for debugging.
     */
    @Override
    public String toString() {
        Date d = new Date(date);
        return "SensorData{" +
                "date='" + d + '\'' +
                ", id='" + id + '\'' +
                ", size=" + size +
                '}';
    }
}
