package hu.u_szeged.inf.fog.simulator.application;

import java.util.ArrayList;

import hu.mta.sztaki.lpds.cloud.simulator.util.SeedSyncer;
import hu.u_szeged.inf.fog.simulator.physical.ComputingAppliance;

/**
 * This strategy chooses an application randomly and sends 50% of the unprocessed data to that application.
 */
public class RandomApplicationStrategy extends ApplicationStrategy {

   /**
    * Constructor calls the installation process.
    * @param a The application which contains the unprocessed data.
    * @param dataForTransfer The size of the unprocessed data.
    */
   public RandomApplicationStrategy(Application a, long dataForTransfer) {
      this.dataForTransfer = dataForTransfer;
      this.a = a;
      this.install();
   }

   /**
    * Considers the neighbor and parent nodes and chooses one randomly.
    */
   @Override
   public void install() {
      ArrayList < ComputingAppliance > caList = new ArrayList < ComputingAppliance > ();
      caList.addAll(a.computingAppliance.neighborList);
      if (a.computingAppliance.parentNode != null) {
         caList.add(a.computingAppliance.parentNode);
      }
      if (caList.size() > 0) {
         ComputingAppliance ca = caList.get(SeedSyncer.centralRnd.nextInt(caList.size()));
         Application chosenApp = ca.applicationList.get(SeedSyncer.centralRnd.nextInt(ca.applicationList.size()));
         a.transferToApplication(chosenApp, this.dataForTransfer / 2);
      }
   }
}