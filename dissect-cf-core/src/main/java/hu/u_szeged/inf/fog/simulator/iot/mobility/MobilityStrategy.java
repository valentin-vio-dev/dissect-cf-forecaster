package hu.u_szeged.inf.fog.simulator.iot.mobility;

public abstract class MobilityStrategy {
	
	double speed;
	
	GeoLocation currentPosition;
	
	public abstract GeoLocation move(long freq);
}