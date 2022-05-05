package hu.u_szeged.inf.fog.simulator.iot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.u_szeged.inf.fog.simulator.application.Application;
import hu.u_szeged.inf.fog.simulator.pliant.FuzzyIndicators;
import hu.u_szeged.inf.fog.simulator.pliant.Kappa;
import hu.u_szeged.inf.fog.simulator.pliant.Sigmoid;

/**
 * Fuzzy-based strategy.. TODO: refactor! 
 */
public class FuzzyDeviceStrategy extends DeviceStrategy {

    /**
     * The local copy of the device which needs to be installed.
     */
    Device d;

    /**
     * Constructor calls the installation process.
     * @param d The device which needs to be installed/paired.
     */
    public FuzzyDeviceStrategy() {}

    /**
     * Fuzzy-based strategy.. TODO: refactor!
     */
    @Override
    public void install() {
    	int appIndex = fuzzyDecision(d);
        if (Application.allApplication.get(appIndex) == null) {
        	System.err.println("There is no possible application for the data transfer!");
            System.exit(0);
        }
        chosenApplication = Application.allApplication.get(appIndex);
    }
    
    /**
     * Fuzzy-based strategy.. TODO: refactoring! 
     */
    private int fuzzyDecision(Device d) {
        List < Application > availableApplications = new ArrayList < Application > ();

        for (Application app: Application.allApplication) {
            if (app.canJoin) {
                availableApplications.add(app);
            }
        }

        if (availableApplications.size() > 0) {
            Kappa kappa = new Kappa(3.0, 0.4);
            Sigmoid < Object > sig = new Sigmoid < Object > (Double.valueOf(-1.0 / 96.0), Double.valueOf(15));
            Vector < Double > price = new Vector < Double > ();
            for (int i = 0; i < availableApplications.size(); ++i) {
                price.add(kappa.getAt(sig.getat(availableApplications.get(i).instance.pricePerTick * 1000000000)));

            }

            double minprice = Double.MAX_VALUE;
            double maxprice = Double.MIN_VALUE;
            for (int i = 0; i < availableApplications.size(); ++i) {
                double currentprice = availableApplications.get(i).getCurrentCost();
                if (currentprice > maxprice)
                    maxprice = currentprice;
                if (currentprice < minprice)
                    minprice = currentprice;
            }

            Vector < Double > currentprice = new Vector < Double > ();
            sig = new Sigmoid < Object > (Double.valueOf(-1.0), Double.valueOf((maxprice - minprice) / 2.0));
            for (int i = 0; i < availableApplications.size(); ++i) {
                currentprice.add(kappa.getAt(sig.getat(availableApplications.get(i).getCurrentCost())));
            }

            double minworkload = Double.MAX_VALUE;
            double maxworkload = Double.MIN_VALUE;
            for (int i = 0; i < availableApplications.size(); ++i) {
                double workload = availableApplications.get(i).computingAppliance.getLoadOfResource();
                if (workload > maxworkload)
                    maxworkload = workload;
                if (workload < minworkload)
                    minworkload = workload;
            }

            Vector < Double > workload = new Vector < Double > ();
            sig = new Sigmoid < Object > (Double.valueOf(-1.0), Double.valueOf(maxworkload));
            for (int i = 0; i < availableApplications.size(); ++i) {
                workload.add(kappa.getAt(sig.getat(availableApplications.get(i).computingAppliance.getLoadOfResource())));

            }

            Vector < Double > numberofvm = new Vector < Double > ();
            sig = new Sigmoid < Object > (Double.valueOf(-1.0 / 8.0), Double.valueOf(3));
            for (int i = 0; i < availableApplications.size(); ++i) {
                numberofvm.add(kappa.getAt(sig.getat(Double.valueOf(availableApplications.get(i).vmCollectorList.size()))));
            }

            double sum_stations = 0.0;
            for (int i = 0; i < availableApplications.size(); ++i) {
                sum_stations += availableApplications.get(i).deviceList.size();
            }

            Vector < Double > numberofstation = new Vector < Double > ();
            sig = new Sigmoid < Object > (Double.valueOf(-0.125), Double.valueOf(sum_stations / (availableApplications.size())));
            for (int i = 0; i < availableApplications.size(); ++i) {
                numberofstation.add(kappa.getAt(sig.getat(Double.valueOf(Application.allApplication.get(i).deviceList.size()))));
            }

            Vector < Double > numberofActiveStation = new Vector < Double > ();
            for (int i = 0; i < availableApplications.size(); ++i) {
                double sum = 0.0;
                for (int j = 0; j < availableApplications.get(i).deviceList.size(); j++) {
                    Station stat = (Station) availableApplications.get(i).deviceList.get(j);
                    long time = Timed.getFireCount();
                    if (stat.startTime >= time && stat.stopTime >= time)
                        sum += 1;
                }
                numberofActiveStation.add(sum);
            }
            
            sum_stations = 0.0;
            for (int i = 0; i < numberofActiveStation.size(); ++i) {
                sum_stations += numberofActiveStation.get(i);
            }

            sig = new Sigmoid < Object > (Double.valueOf(-0.125), Double.valueOf(sum_stations / (numberofActiveStation.size())));
            for (int i = 0; i < numberofActiveStation.size(); ++i) {
                double a = numberofActiveStation.get(i);
                double b = sig.getat(a);
                double c = kappa.getAt(b);
                numberofActiveStation.set(i, c);
            }

            Vector < Double > preferVM = new Vector < Double > ();
            sig = new Sigmoid < Object > (Double.valueOf(1.0 / 32), Double.valueOf(3));
            for (int i = 0; i < availableApplications.size(); ++i) {
                preferVM.add(kappa.getAt(sig.getat(Double.valueOf(availableApplications.get(i).instance.arc.getRequiredCPUs()))));
            }

            Vector < Double > preferVMMem = new Vector < Double > ();
            sig = new Sigmoid < Object > (Double.valueOf(1.0 / 256.0), Double.valueOf(350.0));
            for (int i = 0; i < availableApplications.size(); ++i) {
                preferVMMem.add(kappa.getAt(sig.getat(Double.valueOf(availableApplications.get(i).instance.arc.getRequiredMemory() / 10000000))));
            }

            Vector < Double > score = new Vector < Double > ();
            for (int i = 0; i < price.size(); ++i) {
                Vector < Double > temp = new Vector < Double > ();
                temp.add(price.get(i));
                temp.add(numberofstation.get(i));
                temp.add(numberofActiveStation.get(i));
                temp.add(preferVM.get(i));
                temp.add(workload.get(i));
                temp.add(currentprice.get(i));
                score.add(FuzzyIndicators.getAggregation(temp) * 100);
            }
            
            Vector < Integer > finaldecision = new Vector < Integer > ();
            for (int i = 0; i < availableApplications.size(); ++i) {
                finaldecision.add(i);
            }
            
            for (int i = 0; i < score.size(); ++i) {
                for (int j = 0; j < score.get(i); j++) {
                    finaldecision.add(i);
                }
            }
            
            Random rnd = new Random();
            Collections.shuffle(finaldecision);
            int temp = rnd.nextInt(finaldecision.size());

            return finaldecision.elementAt(temp);
        } else {
            return -1;
        }
    }
}