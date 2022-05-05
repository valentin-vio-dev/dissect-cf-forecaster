package hu.u_szeged.inf.fog.simulator.util;

import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.ConstantConsumptionModel;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.LinearConsumptionModel;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;
import hu.u_szeged.inf.fog.simulator.physical.MicroController;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * This helper class provides a simple way to generate the necessary power
 * transition functions for the microcontroller behavior.
 */
public class MicrocontrollerPowerTransitionGenerator extends PowerTransitionGenerator {

	/**
	 * The generator function that derives the power transition and power state
	 * definitions from a few simple parameters. The generated power states will all
	 * be based on the linear consumption model (except during power off state).
	 * 
	 * @param minpower The power (in W) to be drawn by the MC while it is completely switched off.
	 * @param idlepower The power (in W) to be drawn by the MC's CPU while it is running but not doing any useful tasks.
	 * @param maxpower The power (in W) to be drawn by the MC's CPU if it's CPU is completely utilized.
	 * @param diskDivider The ratio of the MC's disk power draw values compared to 
	 * 	the it'sCPU's power draw values (currently not used!).
	 * @param netDivider the ratio of the MC's network power draw values compared to 
	 * 	the it's CPU's power draw values (currently not used!).
	 */
	public static EnumMap<PowerTransitionGenerator.PowerStateKind, Map<String, PowerState>> generateTransitions(
			double minpower, double idlepower, double maxpower, double diskDivider, double netDivider)
			throws SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		EnumMap<PowerTransitionGenerator.PowerStateKind, Map<String, PowerState>> returner = new EnumMap<PowerTransitionGenerator.PowerStateKind, Map<String, PowerState>>(
				PowerTransitionGenerator.PowerStateKind.class);
		HashMap<String, PowerState> hostStates = new HashMap<String, PowerState>();
		returner.put(PowerTransitionGenerator.PowerStateKind.host, hostStates);
		HashMap<String, PowerState> diskStates = new HashMap<String, PowerState>();
		returner.put(PowerTransitionGenerator.PowerStateKind.storage, diskStates);
		HashMap<String, PowerState> netStates = new HashMap<String, PowerState>();
		returner.put(PowerTransitionGenerator.PowerStateKind.network, netStates);
		PowerState hostDefault = new PowerState(idlepower, maxpower - idlepower, LinearConsumptionModel.class);
		
		hostStates.put(MicroController.State.OFF.toString(), new PowerState(minpower, 0, ConstantConsumptionModel.class));
		hostStates.put(MicroController.State.RUNNING.toString(), hostDefault);
		hostStates.put(MicroController.State.METERING.toString(), new PowerState(idlepower, maxpower-(idlepower/2), LinearConsumptionModel.class));
		
		diskStates.put(NetworkNode.State.OFF.toString(), new PowerState(0, 0, ConstantConsumptionModel.class));
		diskStates.put(NetworkNode.State.RUNNING.toString(), new PowerState(0, 0, ConstantConsumptionModel.class));
		netStates.put(NetworkNode.State.OFF.toString(), new PowerState(0, 0, ConstantConsumptionModel.class));
		netStates.put(NetworkNode.State.RUNNING.toString(), new PowerState(0, 0, ConstantConsumptionModel.class));
		return returner;
	}
}
