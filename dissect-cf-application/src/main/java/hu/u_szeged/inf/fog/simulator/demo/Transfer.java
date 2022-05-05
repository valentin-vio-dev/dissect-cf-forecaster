package hu.u_szeged.inf.fog.simulator.demo;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ConsumptionEventAdapter;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.StorageObject;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Transfer extends ConsumptionEventAdapter{
	
	Repository from;
	Repository to;
	StorageObject so;
	long start;
	static int SIZE = 50;
	
	public Transfer(Repository from, Repository to, StorageObject so) throws NetworkException{
		this.from = from;
		this.to = to;
		this.so = so;
		this.from.registerObject(so);
		this.from.requestContentDelivery(so.id, to, this);
		this.start=Timed.getFireCount();
	}
	
	@Override
	public void conComplete() {
		this.from.deregisterObject(this.so);
		//@ VIO_REMOVED_COMMENT @ System.out.println("Start: "+this.start+" from: "+this.from.getName()+" to: "+this.to.getName()+" end: "+Timed.getFireCount());
	}
	
	public static void main(String[] args) throws Exception {
	
		final EnumMap<PowerTransitionGenerator.PowerStateKind, Map<String, PowerState>> transitions =
				PowerTransitionGenerator.generateTransitions(20, 296, 493, 50, 108);
		
		HashMap<String, Integer> latencyMap = new HashMap<String, Integer>();
		
		Repository r1 = new Repository(1024, "r1", 1000, 1000, 1000, latencyMap, 
				transitions.get(PowerTransitionGenerator.PowerStateKind.storage),transitions.get(PowerTransitionGenerator.PowerStateKind.network));
		
		Repository r2 = new Repository(1024, "r2", 1000, 1000, 1000, latencyMap,
				transitions.get(PowerTransitionGenerator.PowerStateKind.storage),transitions.get(PowerTransitionGenerator.PowerStateKind.network));
		
		Repository r3 = new Repository(1024, "r3", 1000, 1000, 1000, latencyMap,
				transitions.get(PowerTransitionGenerator.PowerStateKind.storage),transitions.get(PowerTransitionGenerator.PowerStateKind.network));

		r1.setState(NetworkNode.State.RUNNING);
		r2.setState(NetworkNode.State.RUNNING);
		r3.setState(NetworkNode.State.RUNNING);

		latencyMap.put("r1",6);
		latencyMap.put("r2",5);
		latencyMap.put("r3",6);
		
		new Transfer(r1,r2,new StorageObject("s12",SIZE,false));
		new Transfer(r1,r3,new StorageObject("s13",SIZE,false));
		
		new Transfer(r2,r3,new StorageObject("s23",SIZE,false));
		new Transfer(r2,r1,new StorageObject("s21",SIZE,false));
		
		new Transfer(r3,r2,new StorageObject("s32",SIZE,false));
		new Transfer(r3,r1,new StorageObject("s31",SIZE,false));

		Timed.simulateUntilLastEvent();

		//@ VIO_REMOVED_COMMENT @ System.out.println("r1: "+r1.contents());
		//@ VIO_REMOVED_COMMENT @ System.out.println("r2: "+r2.contents());
		//@ VIO_REMOVED_COMMENT @ System.out.println("r3: "+r3.contents());
	}

}