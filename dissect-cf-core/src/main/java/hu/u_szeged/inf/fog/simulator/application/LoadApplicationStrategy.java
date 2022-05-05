package hu.u_szeged.inf.fog.simulator.application;

import java.util.ArrayList;

import hu.mta.sztaki.lpds.cloud.simulator.util.SeedSyncer;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

/**
 * This strategy tries to find the less loaded computing resources with the best latency. 
 * 50% of the unprocessed data will be forwarded.
 */
public class LoadApplicationStrategy extends ApplicationStrategy {

   /**
    * Constructor calls the installation process.
    * @param a The application which contains the unprocessed data.
    * @param dataForTransfer The size of the unprocessed data.
    */
   public LoadApplicationStrategy(Application a, long dataForTransfer) {
      this.dataForTransfer = dataForTransfer;
      this.a = a;
      this.install();
   }

   /**
    * If it finds a less loaded node with better latency, it send 50% of the unprocessed data to it.
    */
   @Override
   public void install() {
      ArrayList < ComputingAppliance > caList = new ArrayList < ComputingAppliance > ();
      caList.addAll(a.computingAppliance.neighborList);
      if (a.computingAppliance.parentNode != null) {
         caList.add(a.computingAppliance.parentNode);
      }
      if (caList.size() > 0) {
         ComputingAppliance chosen = caList.get(0);
         for (ComputingAppliance ca: caList) {
            int lat1 = chosen.iaas.repositories.get(0).getLatencies().get(ca.iaas.repositories.get(0).getName());
            int lat2 = ca.iaas.repositories.get(0).getLatencies().get(ca.iaas.repositories.get(0).getName());
            if (chosen.getLoadOfResource() > ca.getLoadOfResource() && lat1 > lat2) {
               chosen = ca;
            }
            Application chosenApp = chosen.applicationList.get(SeedSyncer.centralRnd.nextInt(chosen.applicationList.size()));
            //@ VIO_REMOVED_COMMENT @ System.out.println(this.dataForTransfer);
            this.a.transferToApplication(chosenApp, this.dataForTransfer / 2);
         }
      }
   }
}