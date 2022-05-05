package hu.u_szeged.inf.fog.simulator.iot.mobility;


/**
 * This can represent any point located at the earth.
 */
public class GeoLocation {

	/**
	 * The radius of the earth in kilometers.
	 */
    private static final double EARTH_RADIUS = 6378.137; 
    
    /**
     * Latitude of the point (aka Y).
     */
    double latitude;
    
    /**
     * Longitude of the point (aka X).
     */
    double longitude;
    
    /**
     * TODO: ?
     */
    private boolean weightPoint;

    /**
     * It creates a position on the earth.
     * @param latitude Latitude of the position (aka Y).
     * @param longitude Longitude of the position (aka X).
     */
    public GeoLocation(double latitude, double longitude) {
    	this.weightPoint = true;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * It calculates the distance in meters between two GeoLocation points by using Haversine formula
     * @param other The point which will used for calculating the distance
     */
    public double calculateDistance(GeoLocation other) {
        double o_longitude = other.longitude;
        double o_latitude = other.latitude;
        double d_lat = o_latitude * Math.PI / 180 - this.latitude * Math.PI / 180;
        double d_long = o_longitude * Math.PI / 180 - this.longitude * Math.PI / 180;
        double a = Math.sin(d_lat/2) * Math.sin(d_lat/2) +
                Math.cos(this.latitude * Math.PI / 180) * Math.cos(o_latitude * Math.PI / 180) *
                Math.sin(d_long/2) * Math.sin(d_long/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = EARTH_RADIUS * c;
        return d * 1000;
    }

    /**
     * ToString method for debugging.
     */
	@Override
	public String toString() {
		return "GeoLocation [latitude=" + latitude + ", longitude=" + longitude + ", weightPoint=" + weightPoint + "]";
	}
}
