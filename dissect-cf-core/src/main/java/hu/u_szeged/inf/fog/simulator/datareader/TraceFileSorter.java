package hu.u_szeged.inf.fog.simulator.datareader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This static class is able to fully refactor and sort a trace file through the sortFile method.
 */
public abstract class TraceFileSorter{

    /**
     * This function creates data objects from the rows of the given trace file through the SensorDataReader input parameter.
     * The RowData object contains the UNIX long format of the date from a row, and assembles every other columns after that into one string.
     * @param file A SensorDataReader object to read the file.
     */
    private static ArrayList<RowData> ReadAllLines(SensorDataReader file) {
        ArrayList<RowData> dataList = new ArrayList<>();
        String date;
        //Date Column index validation. -1 or greater is needed, -1 means there is no second date column.
        for(int i : file.dateColumn){
            if(i < 0 && i != SensorDataReader.NO_SECOND_DATE_COLUMN){
                throw new IllegalArgumentException("Column and row id-s must be higher than 0 (-1 in case of id column)");
            }
        }
        //We scan through the whole file at once
        try (Scanner scanner = new Scanner(new File(file.path))) {
            if(file.hasColumnHeader){
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String[] split = scanner.nextLine().split(file.separator);

                //Determine, if the file stores date in two separated columns or not, and make it into one variable
                if(file.dateColumn[1] == SensorDataReader.NO_SECOND_DATE_COLUMN){
                    date = split[file.dateColumn[0]];
                }
                else{
                    date = split[file.dateColumn[0]] + " " + split[file.dateColumn[1]];
                }

                //This piece collects every data which are generated from the sensor, except the time
                String rawData = "";
                for (int i = 0; i < split.length; i++) {
                    if(i != file.dateColumn[0] && i != file.dateColumn[1]){
                        rawData += "," + split[i];
                    }
                }
                //Instantiate a new data object, containing only the date, and everything else in order.
                dataList.add(new RowData(file.getDate(date, file.timePattern), rawData));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * This function gets a file as a SensorDataReader object, and a filename string to name the newly created and sorted file.
     * It uses the SensorDataReader object's functions to read the original file, so this parameter has to be valid.
     * It sorts the file by the read data object's date attribute which is a long UNIX format.
     * Then it refactors the file to traditional csv, with the zero index column is the date in yyyy-MM-dd HH:mm:ss and commas as separators
     * @param file SensorDataReader object
     * @param filename the name and path of the new file
     */
    public static void sortFile(SensorDataReader file, String filename){
        ArrayList<RowData> dataList = ReadAllLines(file);
        Comparator<RowData> compareByTime = (RowData o1, RowData o2) -> Long.valueOf(o1.date).compareTo(o2.date);
        Collections.sort(dataList, compareByTime);
        writeFile(dataList, filename);
    }

    /**
     * This function writes the sorted data into a file.
     * @param dataList The sorted data objects in an ArrayList
     * @param filename The name and the path of the new file
     */
    private static void writeFile(ArrayList<RowData> dataList, String filename){
        try {
            String name = filename;
            FileWriter file = new FileWriter(name);
            BufferedWriter output = new BufferedWriter(file);
            for (var item : dataList) {
                output.write(item + "");
                output.newLine();
            }
            output.close();
        }
        catch (Exception e) {
            e.getStackTrace();
        }
    }

    /**
     * This class represents the rows of the trace file only by the date and every other data as a string.
     * It is built to be easily sorted by the date.
     */
    private static class RowData{
    	
    	/**
    	 * The date which is located in the actual row of a file.
    	 */
        public final long date;
        
        /**
         * The rest of the information (except the date) which is located in the actual row of a file.
         */
        public final String data;

        /**
         * It transfers a row of a file into an object.
         * @param date The date located in a row.
         * @param data The rest of the content located in a row.
         */
        public RowData(long date, String data){
            this.date = date;
            this.data = data;
        }

        /**
         * ToString method is useful for debugging.
         */
        @Override
        public String toString() {
            Date d=new Date(date);
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateText = df2.format(d);
            return dateText + data;
        }


    }
}