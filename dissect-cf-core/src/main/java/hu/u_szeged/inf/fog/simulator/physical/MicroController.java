package hu.u_szeged.inf.fog.simulator.physical;

import java.util.EnumSet;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.notifications.SingleNotificationHandler;
import hu.mta.sztaki.lpds.cloud.simulator.notifications.StateDependentEventHandler;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;

/**
 * The class represent the background power of a smart device (computing, storing, networking).
 * Thus the energy consumptions of devices can be calculated based on three states: OFF, RUNNING, METERING
 */
public class MicroController extends PhysicalMachine {

  /** 
   * OFF: when the device is turned off.
   * RUNNING: when the device is running, but no active jobs are in progress.
   * METERING: represents periods of high energy need (e.g. sampling the environment).
   */
  public static enum State {
    OFF,
    RUNNING,
    METERING
  };

  /**
   * List of MC states that mostly consume energy.
   */
  public static final EnumSet < State > StatesOfHighEnergyConsumption = EnumSet.of(State.RUNNING, State.METERING);

  /**
   * The actual state of the object.
   */
  private State currentState;

  /**
   * It creates a MC initially with OFF state.
   * @param cores The number of CPU cores.
   * @param perCorePocessing The processing capabilities of a single CPU core.
   * @param memory The size of the physical memory.
   * @param disk The size of the local disk.
   * @param onD Number of instructions to simulate the turning on process.
   * @param offD Number of instructions to simulate the turning off process.
   * @param cpuPowerTransitions Defines the energy consumption behavior of the MC.
   */
  public MicroController(double cores, double perCorePocessing, long memory, Repository disk, int onD, int offD,
    Map < String, PowerState > cpuPowerTransitions) {
    super(cores, perCorePocessing, memory, disk, onD, offD, cpuPowerTransitions);
    this.currentState = MicroController.State.OFF;
  }

  /**
   * True, if the actual state is RUNNING.
   */
  public boolean isRunning() {
    if (this.currentState == State.RUNNING) {
      return true;
    }
    return false;
  }

  /**
   * True, if the actual state is METERING.
   */
  public boolean isMetering() {
    if (this.currentState == State.METERING) {
      return true;
    }
    return false;
  }

  /**
   * True, if the actual state is OFF.
   */
  public boolean isSwitchedOff() {
    if (this.currentState == State.OFF) {
      return true;
    }
    return false;
  }

  /**
   * It changes the actual state to RUNNING.
   */
  public void setStateToRunning() {
    switch (this.currentState) {
    case OFF:
      try {
        this.setState(MicroController.State.RUNNING);
      } catch (NetworkException nex) {
        throw new RuntimeException(nex);
      }
      break;
    case RUNNING:
      System.err.println("WARNING: an already running MC was tasked to run!");
      System.exit(0); // TODO: remove
      break;
    case METERING:
      try {
        this.setState(MicroController.State.RUNNING);
      } catch (NetworkException nex) {
        throw new RuntimeException(nex);
      }
      break;
    }
  }

  /**
   * It changes the actual state to OFF.
   */
  public void setStateToOff() {
    switch (this.currentState) {
    case OFF:
      System.err.println("WARNING: an already switched off MC was tasked to switch off!");
      System.exit(0); // TODO: remove
      break;
    case RUNNING:
      try {
        this.setState(MicroController.State.OFF);
      } catch (NetworkException nex) {
        throw new RuntimeException(nex);
      }
      break;
    case METERING:
      try {
        this.setState(MicroController.State.RUNNING);
      } catch (NetworkException nex) {
        throw new RuntimeException(nex);
      }
      this.setStateToOff();
      break;
    }
  }

  /**
   * It changes the actual state to METERING.
   */
  public void setStateToMetering() throws NetworkException {
    switch (this.currentState) {
    case OFF:
      System.err.println("WARNING: you cannot set metering state if MC is turned off!");
      System.exit(0); // TODO: remove
      break;
    case RUNNING:
      try {
        this.setState(MicroController.State.METERING);
      } catch (NetworkException nex) {
        throw new RuntimeException(nex);
      }

      break;
    case METERING:
      System.err.println("WARNING: an already metering MC was tasked to meter!");
      System.exit(0); // TODO: remove
      break;
    }
  }

  /**
   * Defines the minimal interface for listeners on MC state changes.
   */
  public interface StateChangeListener {

    /**
     * This function is called when a MC state changes. 
     * @param mc The object which will change its state.
     * @param oldState The previous state of the object.
     * @param newState The future state of the object.
     */
    void stateChanged(MicroController mc, State oldState, State newState);
  }

	/**
	 * The manager of the MC state change notifications.
	 */
  public final StateDependentEventHandler < StateChangeListener, Pair < State, State >> stateListenerManager = new StateDependentEventHandler < MicroController.StateChangeListener, Pair < State, State >> (
    new SingleNotificationHandler < StateChangeListener, Pair < State, State >> () {

      @Override
      public void sendNotification(final StateChangeListener onObject, final Pair < State, State > states) {
        onObject.stateChanged(MicroController.this, states.getLeft(), states.getRight());
      }
    });

  /**
   * It manages the state change operation of the MC.
   * @param newState the new MC state to be set.
   */
  private void setState(final State newState) throws NetworkException {
    try {
      localDisk.setState(NetworkNode.State.valueOf(newState.name()));
    } catch (IllegalArgumentException e) {}
    final State pastState = currentState;
    currentState = newState;
    directConsumerUsageMoratory = newState != State.RUNNING;
    stateListenerManager.notifyListeners(Pair.of(pastState, newState));

    setCurrentPowerBehavior(PowerTransitionGenerator.getPowerStateFromMap(hostPowerBehavior, newState.toString()));
  }

}