package hu.u_szeged.inf.fog.simulator.iot.mobility;

import hu.mta.sztaki.lpds.cloud.simulator.util.SeedSyncer;

public class RandomMobilityStrategy extends MobilityStrategy {

    GeoLocation startPosition;
    double radius;
    static final long LONG_RATIO = 40075000;
    static final long LAT_RATIO = 111320;

    public RandomMobilityStrategy(GeoLocation currentPosition, double speed, double radius) {
        this.currentPosition = currentPosition;
        this.radius = radius;
        this.speed = speed;
        this.startPosition = new GeoLocation(currentPosition.latitude, currentPosition.longitude);
    }

    @Override
    public GeoLocation move(long freq) {
        double angle = (SeedSyncer.centralRnd.nextDouble() * 360) * Math.PI / 180; 
        double posX = currentPosition.longitude;
        double posY = currentPosition.latitude;
        posX += Math.cos(angle) * movedLong(freq, posX);
        posY += Math.sin(angle) * movedLat(freq);
        double distance = startPosition.calculateDistance(new GeoLocation(posY, posX));
        if(distance < radius) {
            currentPosition.longitude=posX;
            currentPosition.latitude=posY;
            return currentPosition;
        } else {
            return null;
        }

    }

    private double movedLong(long freq, double latitude) {
        double d = freq * speed;
        return (d/LONG_RATIO) * Math.cos(latitude * Math.PI/180) / 360;
    }

    private double movedLat(long freq) {
        return freq * speed / LAT_RATIO;
    }

}