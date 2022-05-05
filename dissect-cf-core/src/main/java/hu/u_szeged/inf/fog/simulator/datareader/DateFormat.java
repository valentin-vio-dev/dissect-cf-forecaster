package hu.u_szeged.inf.fog.simulator.datareader;

import java.text.SimpleDateFormat;

/**
 * This class is able to store every aspect to process a date-time string.
 * You have to instantiate a new object from this class, if you want to use custom patterns for your file.
 * The main purpose of an object from this class is to be used in SensorDataReader, given as a constructor parameter, 
 * or inside the GetDate method.
 */
public class DateFormat {
	
	/**
	 * The string pattern, for example: "yyyy-MM-dd HH:mm:ss".
	 */
    final SimpleDateFormat dateTimePattern;
    
    /**
     * The character which separates the date and the time from each other.
     */
    final String dateTimeSplitChar;
    
    /**
     * The character which separates the year, month and date from each other.
     */
    final String dateSeparator;
    
    /**
     * The character which separates the hour, minute and seconds from each other. 
     */
    final String timeSeparator;
    
    /**
     * Set this true, if you have 12 hour time system in your file.
     */
    final boolean ampm;

    /**
     * It creates a date-time format in order to using custom patterns for your file.
     * @param dateTimePattern The string pattern for the SimpleDateFormat format function. For example: "yyyy-MM-dd HH:mm:ss"
     * @param dateTimeSplitChar The character which separates the date and the time from each other. Usually a white-space, or 'T'.
     * @param dateSeparator The character which separates the year, month and date from each other. Could be '-' from the example above.
     * @param timeSeparator The character which separates the hour, minute and seconds from each other. Could be ':' from the example above.
     * @param ampm Set this true, if you have 12 hour time system in your file.
     */
    public DateFormat(String dateTimePattern, String dateTimeSplitChar, String dateSeparator, String timeSeparator, boolean ampm) {
        this.dateTimePattern = new SimpleDateFormat(dateTimePattern);
        this.dateTimeSplitChar = dateTimeSplitChar;
        this.dateSeparator = dateSeparator;
        this.timeSeparator = timeSeparator;
        this.ampm = ampm;
    }

}
