package hu.u_szeged.inf.fog.simulator.demo;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;

public class TimedTest extends Timed{

	String id;

	TimedTest(String id, long freq){
		this.id=id;
		subscribe(freq);
	}
	
	@Override
	public void tick(long fires) {
		
		if(Timed.getFireCount()>=500) {
			unsubscribe();
		}
		
		//@ VIO_REMOVED_COMMENT @ System.out.println(this.id+" - time: "+ Timed.getFireCount());
	}

	public static void main(String[] args) {

		new TimedTest("tt1", 100);
		new TimedTest("tt2", 99);

		Timed.simulateUntilLastEvent();
	}
}