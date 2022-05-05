package hu.u_szeged.inf.fog.simulator.application;

/**
 * An abstract class to create any arbitrary logic how the unprocessed data will be forwarded to an other IoT application.
 */
public abstract class ApplicationStrategy {

   /**
    * The actual amount of unprocessed data.
    */
   public long dataForTransfer;

   /**
    * The application which should deal with the unprocessed data.
    */
   public Application a;

   /**
    * This method needs to be overridden by realizing the actual policy/logic 
    * to define how many bytes of data to be sent to an other application.
    */
   public abstract void install();

}