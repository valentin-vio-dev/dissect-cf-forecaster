package hu.u_szeged.inf.fog.simulator.demo;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.AlterableResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ConsumptionEventAdapter;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;

public class VmTask {

	public static void main(String[] args) throws Exception {
		
		final EnumMap<PowerTransitionGenerator.PowerStateKind, Map<String, PowerState>> transitions =
				PowerTransitionGenerator.generateTransitions(20, 296, 493, 50, 108);
		
		HashMap<String, Integer> latencyMap = new HashMap<String, Integer>();

		Repository repo = new Repository(17179869184L, "repo", 1000000, 1000000, 1000000, latencyMap, 
				transitions.get(PowerTransitionGenerator.PowerStateKind.storage),transitions.get(PowerTransitionGenerator.PowerStateKind.network));
		
		repo.setState(NetworkNode.State.RUNNING);

		PhysicalMachine pm = new PhysicalMachine(8, 1, 8589934592L,repo, 10, 10,transitions.get(PowerTransitionGenerator.PowerStateKind.host));

		pm.turnon();

		Timed.simulateUntilLastEvent();

		//@ VIO_REMOVED_COMMENT @ System.out.println("Time: "+Timed.getFireCount()+ " PM state: "+pm.getState());

		VirtualAppliance va = new VirtualAppliance("va", 800, 0, false, 1073741824L);
		
		repo.registerObject(va);
		
		AlterableResourceConstraints arc = new AlterableResourceConstraints(4,1,4294967296L);

		VirtualMachine vm = pm.requestVM(va, arc, repo, 1)[0];
				
		Timed.simulateUntilLastEvent();

		//@ VIO_REMOVED_COMMENT @ System.out.println("Time: "+Timed.getFireCount()+ " PM state: "+pm.getState()+ " VM state: "+vm.getState());
		
		vm.newComputeTask(100000, ResourceConsumption.unlimitedProcessing, new ConsumptionEventAdapter() {

			@Override
			public void conComplete() {
				//@ VIO_REMOVED_COMMENT @ System.out.println("Time: "+Timed.getFireCount());
			}
		});
		
		Timed.simulateUntilLastEvent();
	}
	
}
