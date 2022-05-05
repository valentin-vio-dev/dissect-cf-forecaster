package hu.u_szeged.inf.fog.simulator.demo;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;

public class DeferredEventTest extends DeferredEvent{

	public DeferredEventTest(long delay) {
		super(delay);
	}

	@Override
	protected void eventAction() {
		new TimedTest("tt2", 25);
	}
	
public static void main(String[] args) {

		new TimedTest("tt1", 100);
		new DeferredEventTest(300);

		Timed.simulateUntilLastEvent();
	}
}
