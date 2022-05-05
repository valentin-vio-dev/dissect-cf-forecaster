package hu.u_szeged.inf.fog.simulator.iot.mobility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class LinearMobilityStrategy extends MobilityStrategy {

	public Queue<GeoLocation> destinations = new LinkedList<GeoLocation>();

    public LinearMobilityStrategy(GeoLocation currentPosition, double speed, GeoLocation ... destination) {
        this.currentPosition = currentPosition;
        this.destinations.addAll(Arrays.asList(destination));
        this.speed = speed;
    }
    
    public LinearMobilityStrategy(GeoLocation currentPosition, double speed, ArrayList<GeoLocation> destination) {
        this.currentPosition = currentPosition;
        this.destinations.addAll(destination);
        this.speed = speed;
    }

    @Override
    public GeoLocation move(long freq) {
        return setPosition(speed *freq);
    }

    private GeoLocation setPosition(double travelDistance) {
        GeoLocation dest = null;
        if(!destinations.isEmpty()) {
            dest = destinations.peek();

            if (dest != null) {
                double distance = currentPosition.calculateDistance(dest);
                if (distance > travelDistance) {
                    double posX = dest.longitude - currentPosition.longitude;
                    double posY = dest.latitude - currentPosition.latitude;
                    double norm_posX = posX / distance;
                    double norm_posY = posY / distance;
                    currentPosition.longitude = (currentPosition.longitude + norm_posX * travelDistance);
                    currentPosition.latitude = (currentPosition.latitude + norm_posY * travelDistance);
                    return currentPosition;
                } else {
                    double remained = travelDistance - distance;
                    currentPosition = destinations.poll();
                    return setPosition(remained);
                }
            }
        }
        return null;
    }
}
