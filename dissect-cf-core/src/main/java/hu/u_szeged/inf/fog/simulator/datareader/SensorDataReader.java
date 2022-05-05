package hu.u_szeged.inf.fog.simulator.datareader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * This class is able to read from trace files and convert it into SensorData which is the structured representation of the raw data
 *  from a row of a trace file. It is able to read the whole file at once, or row by row, but at large files 
 *  it is not recommended to use the row by row function. It contains a lot of useful constant strings, to help the user 
 *  with the reading parameters.  First you have column constants for id and date columns. Secondly you have the most frequent 
 *  separators as constants. Lastly you have some time patterns which are easy to use, as they are implemented in the code base,
 *  alternatively you can use a DateFormat object.
 */
public class SensorDataReader {
	
    //constants to represent files without id column, and without secondary date column
    public static final int NO_ID_COLUMN = -1;
    public static final int NO_SECOND_DATE_COLUMN = -1;

    //Separators for the columns of the file
    public static final String CSV_SEMICOLON_SEPARATOR = ";";
    public static final String CSV_COMMA_SEPARATOR = ",";
    public static final String SIMPLE_SPACE_SEPARATOR = " ";
    public static final String TABULATED_TXT_SEPARATOR = "  ";
    public static final String QUOTATION_SEPARATOR = "\"";

    //Time patterns for the getdate method
    public static final String PATTERN_yyyyMMdd_HHmmssSSSSS = "yyyy-MM-dd HH:mm:ss.SSSSS";
    public static final String PATTERN_yyyyMMdd_HHmmss= "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_yyyy_T_MMdd_HHmmss= "yyyy-MM-dd'T'HH:mm:ss";
    public static final String PATTERN_yyyy_Per_MM_Per_dd_HHmmssAMPM = "yyyy/MM/dd HH:mm:ss a";
    public static final String PATTERN_dd_Per_MM_Per_yyyy_HHmmssAMPM = "dd/MM/yyyy HH:mm:ss a";
    public static final String PATTERN_MM_Per_dd_Per_yyyy_HHmmssAMPM = "MM/dd/yyyy HH:mm:ss a";
    public static final String PATTERN_dd_Per_MM_Per_yyyy_HHmmss= "dd/MM/yyyy HH:mm:ss";
    public static final String PATTERN_MM_Per_dd_Per_yyyy_HHmmss = "MM/dd/yyyy HH:mm:ss";
    public static final String PATTERN_yyyy_Per_MM_Per_dd_HHmmss = "yyyy/MM/dd HH:mm:ss";
    public static final String PATTERN_ddMMyyyy_HHmmss= "dd.MM.yyyy HH:mm:ss";
    public static final String PATTERN_milisec= "milisec";
    public static final String PATTERN_sec= "sec";


    /**
     * The relative path of the file from this class.
     */
    final String path;
    
    /**
     * A string which splits the file to columns.
     */
    final String separator;
    
    /**
     * Choose one of the valid time patterns stored as constant variables in this class.
     */
    final String timePattern;
    
    /**
     * Boolean stores if a file has column header or not as first row.
     */
    final boolean hasColumnHeader;
    
    /**
     * Index of the date column. Use array if the date and time is separated into two columns.
     */
    int[] dateColumn;
    
    /**
     * Optional, the index of the id column.
     */
    private int idColumn;
    
    
    /**
     * Use DateFormat object to create your own time patterns.
     */
    private DateFormat dateFormat;

    /**
     * It sets up the key properties which are essential to transform trace files into simulation entities.
     * @param path The relative path of the file from this class.
     * @param separator A string which splits the file to columns.
     * @param timePattern Choose one of the valid time patterns stored as constant variables in this class, or use DateFormat object to create your own.
     * @param hasColumnHeader Boolean stores if a file has column header or not as first row.
     * @param dateColumn Index of the date column. Use array if the date and time is separated into two columns.
     * @param idColumn Optional, the index of the id column.
     */
    public SensorDataReader(String path, String separator, String timePattern, boolean hasColumnHeader, int dateColumn, int idColumn) {
        this.path = path;
        this.separator = separator;
        this.timePattern = timePattern;
        this.hasColumnHeader = hasColumnHeader;
        if(ColumnValidator(dateColumn))
            this.dateColumn = new int[] {dateColumn, NO_SECOND_DATE_COLUMN};
        if(ColumnValidator(idColumn))
            this.idColumn = idColumn;
    }

    /**
     * We tried to cover as many constructions as possible. 
     * Please look around which other  constructors are available if it does not fit for you. 
     */
    public SensorDataReader(String path, String separator, String timePattern, boolean hasColumnHeader, int dateColumn) {
        this.path = path;
        this.separator = separator;
        this.timePattern = timePattern;
        this.hasColumnHeader = hasColumnHeader;
        if(ColumnValidator(dateColumn))
            this.dateColumn = new int[] {dateColumn, NO_SECOND_DATE_COLUMN};
        this.idColumn = NO_ID_COLUMN;
    }

    /**
     * We tried to cover as many constructions as possible. 
     * Please look around which other  constructors are available if it does not fit for you. 
     */
    public SensorDataReader(String path, String separator, String timePattern, boolean hasColumnHeader, int[] dateColumn, int idColumn) {
        this.path = path;
        this.separator = separator;
        this.timePattern = timePattern;
        this.hasColumnHeader = hasColumnHeader;
        if(ColumnValidator(dateColumn))
            this.dateColumn = dateColumn;
        if(ColumnValidator(idColumn))
            this.idColumn = idColumn;
    }

    /**
     * We tried to cover as many constructions as possible. 
     * Please look around which other  constructors are available if it does not fit for you. 
     */
    public SensorDataReader(String path, String separator, String timePattern, boolean hasColumnHeader, int[] dateColumn) {
        this.path = path;
        this.separator = separator;
        this.timePattern = timePattern;
        this.hasColumnHeader = hasColumnHeader;
        if(ColumnValidator(dateColumn))
            this.dateColumn = dateColumn;
        this.idColumn = NO_ID_COLUMN;
    }

    /**
     * We tried to cover as many constructions as possible. 
     * Please look around which other  constructors are available if it does not fit for you. 
     */
    public SensorDataReader(String path, String separator, DateFormat dateFormat, boolean hasColumnHeader, int dateColumn, int idColumn) {
        this.path = path;
        this.separator = separator;
        this.timePattern = "timePattern";
        this.dateFormat = dateFormat;
        this.hasColumnHeader = hasColumnHeader;
        if(ColumnValidator(dateColumn))
            this.dateColumn = new int[] {dateColumn, NO_SECOND_DATE_COLUMN};
        if(ColumnValidator(idColumn))
            this.idColumn = idColumn;
    }

    /**
     * We tried to cover as many constructions as possible. 
     * Please look around which other  constructors are available if it does not fit for you. 
     */
    public SensorDataReader(String path, String separator, DateFormat dateFormat, boolean hasColumnHeader, int dateColumn) {
        this.path = path;
        this.separator = separator;
        this.timePattern = "timePattern";
        this.dateFormat = dateFormat;
        this.hasColumnHeader = hasColumnHeader;
        if(ColumnValidator(dateColumn))
            this.dateColumn = new int[] {dateColumn, NO_SECOND_DATE_COLUMN};
        this.idColumn = NO_ID_COLUMN;
    }

    /**
     * We tried to cover as many constructions as possible. 
     * Please look around which other  constructors are available if it does not fit for you. 
     */
    public SensorDataReader(String path, String separator, DateFormat dateFormat, boolean hasColumnHeader, int[] dateColumn, int idColumn) {
        this.path = path;
        this.separator = separator;
        this.timePattern = "timePattern";
        this.dateFormat = dateFormat;
        this.hasColumnHeader = hasColumnHeader;
        if(ColumnValidator(dateColumn))
            this.dateColumn = dateColumn;
        if(ColumnValidator(idColumn))
            this.idColumn = idColumn;
    }

    /**
     * We tried to cover as many constructions as possible. 
     * Please look around which other  constructors are available if it does not fit for you. 
     */
    public SensorDataReader(String path, String separator, DateFormat dateFormat, boolean hasColumnHeader, int[] dateColumn) {
        this.path = path;
        this.separator = separator;
        this.timePattern = "timePattern";
        this.dateFormat = dateFormat;
        this.hasColumnHeader = hasColumnHeader;
        if(ColumnValidator(dateColumn))
            this.dateColumn = dateColumn;
        this.idColumn = NO_ID_COLUMN;
    }

   /**
    * This method validates the index of the columns came from the constructor parameters.
    * @param column Use array if the date and time is separated into two columns.
    */
    private boolean ColumnValidator(int[] column){
        for(int i : column){
            if(i < 0 && i != NO_SECOND_DATE_COLUMN){
                throw new IllegalArgumentException("Column id-s must be valid index");
            }
        }
        return true;
    }
    
   /**   
    * This method validates the index of the columns came from the constructor parameters.
    * @param column Use index if the date and time is not separated.
    */
    private boolean ColumnValidator(int column){
        if(column < 0 && column != NO_ID_COLUMN){
            throw new IllegalArgumentException("Column id-s must be valid index");
        }
        return true;
    }

    /**
     * This function reads one selected row from the trace file, converting the raw data into SensorData.
     * It is much slower on large files, than using the ReadAllLines function.
     * @param row index of the current row to read.
     */
    public SensorData ReadData(int row) throws IllegalArgumentException {
    	String date;
        if(row < 0){
            throw new IllegalArgumentException("Targeted row index must be higher or equal to 0");
        }
        //Stream through the file until the row came as param
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            String[] split = lines.skip(row).findFirst().get().split(separator);

            //Determine, if the file stores date in two separated columns or not, and make it into one variable
            if(dateColumn[1] == NO_SECOND_DATE_COLUMN){
                date = split[dateColumn[0]];
            }
            else{
                date = split[dateColumn[0]] + " " + split[dateColumn[1]];
            }

            //This piece collects every data which are generated from the sensor, except the date and the measurement id
            String rawData = "";
            for(int i = 0; i < split.length; i++){
                if(i != dateColumn[0] && i != dateColumn[1] && i != idColumn){
                    rawData += split[i];
                }

            }
            //After we collected all the measurements we convert them into bytes
            final byte[] bytes = rawData.getBytes(StandardCharsets.UTF_8);

            //If we dont have ID column, we have to generate one.
            if(idColumn == NO_ID_COLUMN){
                Random r = new Random();
                String id = row + "_measurement_" + date + "_" + r.nextInt(1000);
                return new SensorData(getDate(date, timePattern), id, bytes.length);
            }else{
                return new SensorData(getDate(date, timePattern), split[idColumn], bytes.length);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This function reads the entire trace file and process it's raw data into a SensorData list.
     * This is the faster solution for reading large files.
     */
    public ArrayList<SensorData> ReadAllLines() {
    	String date; 
        int row = 0; //We need the row id if we have to create an ID for the measurement
        ArrayList<SensorData> dataList = new ArrayList<>();

        //We scan through the whole file at once
        try (Scanner scanner = new Scanner(new File(path))) {
            if(hasColumnHeader){
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String[] split = scanner.nextLine().split(separator);

                //Determine, if the file stores date in two separated columns or not, and make it into one variable
                if(dateColumn[1] == NO_SECOND_DATE_COLUMN){
                    date = split[dateColumn[0]];
                }
                else{
                    date = split[dateColumn[0]] + " " + split[dateColumn[1]];
                }

                //This piece collects every data which are generated from the sensor, except the date and the measurement id
                String rawData = "";
                for (int i = 0; i < split.length; i++) {
                    if(i != dateColumn[0] && i != dateColumn[1] && i != idColumn){
                        rawData += split[i];
                    }
                }

                //After we collected all the measurements we convert them into bytes
                final byte[] bytes = rawData.getBytes(StandardCharsets.UTF_8);

                //If we dont have ID column, we have to generate one.
                if (idColumn == NO_ID_COLUMN) {
                    Random r = new Random();
                    String id = row + "_measurement_" + date + "_" + r.nextInt(1000);
                    dataList.add(new SensorData(getDate(date, timePattern), id, bytes.length));
                }else{
                    dataList.add(new SensorData(getDate(date, timePattern), split[idColumn], bytes.length));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * This function gets a string containing date and time, and a pattern to process them. The function recognizes
     * the date and the time separately, refactor it, then parse it into long milliseconds as the pattern defines.
     * @param rawDateString A string containing time and date.
     * @param pattern Prewritten constant time pattern from the SensorDataReader.java, or any string, to use DateFormat custom pattern.
     */
    public long getDate(String rawDateString, String pattern) throws IllegalArgumentException{
        /*This switch determines the pattern, and instantiate a new dateFormat from it. If the user gives custom dateFormat from the constructor,
        Then the switch jumps to default and leave it as it is. */
        switch(pattern){
            case PATTERN_yyyyMMdd_HHmmssSSSSS:
                dateFormat = new DateFormat(PATTERN_yyyyMMdd_HHmmssSSSSS, " ", "-", ":", false);
                break;
            case PATTERN_yyyyMMdd_HHmmss:
                dateFormat = new DateFormat(PATTERN_yyyyMMdd_HHmmss, " ", "-", ":", false);
                break;

            case PATTERN_yyyy_T_MMdd_HHmmss:
                dateFormat = new DateFormat(PATTERN_yyyyMMdd_HHmmss, "T", "-", ":", false);
                break;

            case PATTERN_ddMMyyyy_HHmmss:
                dateFormat = new DateFormat(PATTERN_ddMMyyyy_HHmmss, " ", ".", ":", false);
                break;

            case PATTERN_dd_Per_MM_Per_yyyy_HHmmss:
                dateFormat = new DateFormat(PATTERN_dd_Per_MM_Per_yyyy_HHmmss, " ", "/", ":", false);
                break;

            case PATTERN_dd_Per_MM_Per_yyyy_HHmmssAMPM:
                dateFormat = new DateFormat(PATTERN_dd_Per_MM_Per_yyyy_HHmmss, " ", "/", ":", true);
                break;

            case PATTERN_MM_Per_dd_Per_yyyy_HHmmssAMPM:
                dateFormat = new DateFormat(PATTERN_MM_Per_dd_Per_yyyy_HHmmss, " ", "/", ":", true);
                break;

            case PATTERN_yyyy_Per_MM_Per_dd_HHmmssAMPM:
                dateFormat = new DateFormat(PATTERN_yyyy_Per_MM_Per_dd_HHmmss, " ", "/", ":", true);
                break;

            case PATTERN_MM_Per_dd_Per_yyyy_HHmmss:
                dateFormat = new DateFormat(PATTERN_MM_Per_dd_Per_yyyy_HHmmss, " ", "/", ":", false);
                break;

            case PATTERN_yyyy_Per_MM_Per_dd_HHmmss:
                dateFormat = new DateFormat(PATTERN_yyyy_Per_MM_Per_dd_HHmmss, " ", "/", ":", false);
                break;

            case PATTERN_milisec:
                return Long.parseLong(rawDateString.split("\\.")[0]);

            case PATTERN_sec:
                return Long.parseLong(rawDateString.split("\\.")[0])*1000;

            default:
                if(dateFormat == null)
                    throw new IllegalArgumentException("Given time pattern is not existing!");
                break;
        }

        //We have to split the date from the time, in order to determine which is which, and reformat them if needed.
        String date = "", time = "";
        String[] split = rawDateString.split(dateFormat.dateTimeSplitChar);
        for(String fdate : split){
            if(fdate.contains(dateFormat.dateSeparator)){
                date = fdate;
            }
        }
        for(String ftime : split){
            if(ftime.contains(dateFormat.timeSeparator)){
                time = ftime;
            }
        }

        //This function call is used if the user choose 12 hour format, but as we work in UNIX, we have to convert it into 24H
        if(dateFormat.ampm){
            time = AMPMto24Hours(time, split[2]);
        }

        String dateTime = date + " " + time;


        try {
            return(dateFormat.dateTimePattern.parse(dateTime).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * This function parses the 12 hour timing to 24 hour timing
     * @param time Hour, minute, and seconds as the following format: HH:mm:ss
     * @param AMPM The AM or PM part of the time string.
     */
    private String AMPMto24Hours(String time, String AMPM){
        SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm:ss");
        try {
            time += " " + AMPM;
            return date24Format.format(date12Format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}