package hu.u_szeged.inf.fog.simulator.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import hu.mta.sztaki.lpds.cloud.simulator.util.SeedSyncer;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;
import hu.u_szeged.inf.fog.simulator.pliant.FuzzyIndicators;
import hu.u_szeged.inf.fog.simulator.pliant.Sigmoid;

/**
 * Fuzzy-based strategy.. TODO: refactor!
 */
public class FuzzyApplicationStrategy extends ApplicationStrategy {

   public FuzzyApplicationStrategy(Application a, long dataForTransfer) {
       this.dataForTransfer = dataForTransfer;
       this.a = a;
       this.install();
    }

   @Override
   public void install() {
      ArrayList < ComputingAppliance > caList = new ArrayList < ComputingAppliance > ();
      caList.addAll(a.computingAppliance.neighborList);
      if (a.computingAppliance.parentNode != null) {
         caList.add(a.computingAppliance.parentNode);
      }
      ComputingAppliance currentCA = a.computingAppliance;
      if (caList.size() > 0) {
         double MinLoadOfResource = currentCA.getLoadOfResource();
         double MaxLoadOfResource = currentCA.getLoadOfResource();
         int deviceMin = currentCA.applicationList.get(0).deviceList.size();
         int deviceMax = currentCA.applicationList.get(0).deviceList.size();
         double MinPrice = currentCA.applicationList.get(0).instance.pricePerTick * 100000000;
         double MaxPrice = currentCA.applicationList.get(0).instance.pricePerTick * 100000000;
         double MinLatency = currentCA.applicationList.get(0).computingAppliance.iaas.repositories.get(0).getLatencies().get(currentCA.iaas.repositories.get(0).getName());
         double MaxLatency = currentCA.applicationList.get(0).computingAppliance.iaas.repositories.get(0).getLatencies().get(currentCA.iaas.repositories.get(0).getName());
         double MinUnprocessedData = (currentCA.applicationList.get(0).sumOfArrivedData - currentCA.applicationList.get(0).sumOfProcessedData) / currentCA.applicationList.get(0).taskSize;
         double MaxUnprocessedData = (currentCA.applicationList.get(0).sumOfArrivedData - currentCA.applicationList.get(0).sumOfProcessedData) / currentCA.applicationList.get(0).taskSize;


         for (int i = 0; i < caList.size(); i++) {
            ComputingAppliance ca = caList.get(i);

           double loadofresource = ca.getLoadOfResource();
            if (loadofresource < MinLoadOfResource)
               MinLoadOfResource = loadofresource;
            if (loadofresource > MaxLoadOfResource)
               MaxLoadOfResource = loadofresource;

            int deviceSize = ca.applicationList.get(0).deviceList.size();
            if (deviceSize < deviceMin)
               deviceMin = deviceSize;
            if (deviceSize > deviceMax)
               deviceMax = deviceSize;

            double priceperTick = ca.applicationList.get(0).instance.pricePerTick * 100000000;
            if (priceperTick < MinPrice)
               MinPrice = priceperTick;
            if (priceperTick > MaxPrice)
               MaxPrice = priceperTick;

             double latency = a.computingAppliance.iaas.repositories.get(0).getLatencies().get(caList.get(i).iaas.repositories.get(0).getName());
            if (latency < MinLatency)
               MinLatency = latency;
            if (latency > MaxLatency)
               MaxLatency = latency;

            double unprocesseddata = (ca.applicationList.get(0).sumOfArrivedData - ca.applicationList.get(0).sumOfProcessedData) / ca.applicationList.get(0).taskSize;
            if (unprocesseddata < MinUnprocessedData)
               MinUnprocessedData = unprocesseddata;
            if (unprocesseddata > MaxUnprocessedData)
               MaxUnprocessedData = unprocesseddata;
         }

         Vector < Double > loadOfResource = new Vector < Double > ();
         Vector < Double > price = new Vector < Double > ();
         Vector < Double > unprocesseddata = new Vector < Double > ();
         //@ VIO_REMOVED_COMMENT @ System.out.println();
         for (int i = 0; i < caList.size(); i++) {

            ComputingAppliance ca = caList.get(i);
             Sigmoid < Object > sig = new Sigmoid < Object > (
               Double.valueOf(-1.0 / 8.0),
               Double.valueOf((MaxLoadOfResource + MinLoadOfResource) / 2.0));
            loadOfResource.add(sig.getat(ca.getLoadOfResource()));

            //@ VIO_REMOVED_COMMENT @ System.out.println(ca.name + " Load Resource " + ca.getLoadOfResource() + " Price: " + ca.applicationList.get(0).instance.pricePerTick * 100000000 + " UnprocessedData: " + (ca.applicationList.get(0).sumOfArrivedData - ca.applicationList.get(0).sumOfProcessedData) / ca.applicationList.get(0).taskSize);

         

            sig = new Sigmoid < Object > (Double.valueOf(4.0 / 1.0), Double.valueOf((MinPrice)));
            price.add(sig.getat(ca.applicationList.get(0).instance.pricePerTick * 100000000));

            //@ VIO_REMOVED_COMMENT @ System.out.println(ca.applicationList.get(0).instance.pricePerTick * 100000000);

            sig = new Sigmoid < Object > (Double.valueOf(-1.0 / 8.0), Double.valueOf((Math.abs((MaxLatency - MinLatency)) / 2.0)));
           
            sig = new Sigmoid < Object > (Double.valueOf(-1.0 / 4.0), Double.valueOf((MaxUnprocessedData - MinUnprocessedData)));
            unprocesseddata.add(sig.getat((double)((
         		   ca.applicationList.get(0).sumOfArrivedData - ca.applicationList.get(0).sumOfProcessedData) / 
         		   ca.applicationList.get(0).taskSize)));
}

         Vector < Integer > score = new Vector < Integer > ();
         for (int i = 0; i < caList.size(); ++i) {
            Vector < Double > temp = new Vector < Double > ();
            temp.add(loadOfResource.get(i));
           
            temp.add(price.get(i));
           
            temp.add(unprocesseddata.get(i));
            score.add((int)(FuzzyIndicators.getAggregation(temp) * 100));
         }
         //@ VIO_REMOVED_COMMENT @ System.out.println("Pontozás: " + score);

        
         Integer currentCAscore;
         Vector < Double > temp = new Vector < Double > ();

         Sigmoid < Object > sig = new Sigmoid < Object > (
            Double.valueOf(-1.0 / 8.0),
            Double.valueOf((MaxLoadOfResource + MinLoadOfResource) / 2.0));
         temp.add(sig.getat(currentCA.getLoadOfResource()));
     
         sig = new Sigmoid < Object > (Double.valueOf(4.0 / 1.0), Double.valueOf((MinPrice)));
         temp.add(sig.getat(currentCA.applicationList.get(0).instance.pricePerTick * 100000000));

         sig = new Sigmoid < Object > (Double.valueOf(-1.0 / 8.0), Double.valueOf((Math.abs((MaxLatency - MinLatency)) / 2.0)));
         
         
         sig = new Sigmoid < Object > (Double.valueOf(-1.0 / 4.0), Double.valueOf((MaxUnprocessedData - MinUnprocessedData)));
         temp.add(sig.getat((double)((currentCA.applicationList.get(0).sumOfArrivedData - currentCA.applicationList.get(0).sumOfProcessedData) / currentCA.applicationList.get(0).taskSize)));

         
         currentCAscore = (int)(FuzzyIndicators.getAggregation(temp) * 100);
         //@ VIO_REMOVED_COMMENT @ System.out.println(currentCA.name + " Load Resource " + currentCA.getLoadOfResource() + " Price: " + currentCA.applicationList.get(0).instance.pricePerTick * 100000000 + " UnprocessedData: " + (currentCA.applicationList.get(0).sumOfArrivedData - currentCA.applicationList.get(0).sumOfProcessedData) / currentCA.applicationList.get(0).taskSize);
         //@ VIO_REMOVED_COMMENT @ System.out.println("Score " + currentCAscore);

         Vector < Integer > finaldecision = new Vector < Integer > ();
         for (int i = 0; i < caList.size(); ++i) {
            finaldecision.add(i);
         }

        
         finaldecision.add(-1);

         for (int i = 0; i < score.size(); ++i) {
            for (int j = 0; j < score.get(i); j++) {
               finaldecision.add(i);
            }
         }

         for (int j = 0; j < currentCAscore; j++)
            finaldecision.add(-1);

         Random rnd = new Random();
         Collections.shuffle(finaldecision);
         int chooseIdx = rnd.nextInt(finaldecision.size());

         if (finaldecision.get(chooseIdx) != -1) {
           ComputingAppliance ca = caList.get(finaldecision.get(chooseIdx));
           Application chosenApp = ca.applicationList.get(SeedSyncer.centralRnd.nextInt(ca.applicationList.size()));
           this.a.transferToApplication(chosenApp, this.dataForTransfer / 2);
         }
      }
   }

}
