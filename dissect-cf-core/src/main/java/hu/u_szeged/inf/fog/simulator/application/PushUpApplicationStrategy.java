package hu.u_szeged.inf.fog.simulator.application;

import hu.mta.sztaki.lpds.cloud.simulator.util.SeedSyncer;

/**
 * Only horizontal data forwarding is allowed. 50% of the unprocessed data will be forwarded.
 */
public class PushUpApplicationStrategy extends ApplicationStrategy {

   /**
    * Constructor calls the installation process.
    * @param a The application which contains the unprocessed data.
    * @param dataForTransfer The size of the unprocessed data.
    */
   public PushUpApplicationStrategy(Application a, long dataForTransfer) {
      this.dataForTransfer = dataForTransfer;
      this.a = a;
      this.install();
   }

   /**
    * It sends the unprocessed data to the parent (if exists).
    */
   @Override
   public void install() {
      if (a.computingAppliance.parentNode != null) {
         this.a.transferToApplication(a.computingAppliance.parentNode.applicationList.get(
               SeedSyncer.centralRnd.nextInt(a.computingAppliance.parentNode.applicationList.size())),
            this.dataForTransfer / 2);
      }
   }
}