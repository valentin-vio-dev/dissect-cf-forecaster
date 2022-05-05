package hu.u_szeged.inf.fog.simulator.application;

import java.util.ArrayList;

import hu.mta.sztaki.lpds.cloud.simulator.util.SeedSyncer;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

/**
 * Only vertical data forwarding is allowed and the less loaded node is preferred.
 * 50% of the unprocessed data will be forwarded.
 */
public class HoldDownApplicationStrategy extends ApplicationStrategy {

   /**
    * Constructor calls the installation process.
    * @param a The application which contains the unprocessed data.
    * @param dataForTransfer The size of the unprocessed data.
    */
   public HoldDownApplicationStrategy(Application a, long dataForTransfer) {
      this.dataForTransfer = dataForTransfer;
      this.a = a;
      this.install();
   }

   /**
    * It sends the unprocessed data to the less loaded neighbor.
    */
   @Override
   public void install() {
      ArrayList < ComputingAppliance > caList = new ArrayList < ComputingAppliance > ();
      caList.addAll(a.computingAppliance.neighborList);
      if (caList.size() > 0) {
         ComputingAppliance chosen = caList.get(0);
         for (ComputingAppliance ca: caList) {
            if (chosen.getLoadOfResource() > ca.getLoadOfResource()) {
               chosen = ca;
            }
            
            Application chosenApp = chosen.applicationList.get(SeedSyncer.centralRnd.nextInt(chosen.applicationList.size()));
            this.a.transferToApplication(chosenApp, this.dataForTransfer / 2);
         }
      }
   }
}
