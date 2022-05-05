package hu.vio.simulator;

/**
 * Class for call given function after certain time.
 */
public class ComputeNodeTimer {

    public interface Notyifyer {
        void notifyListener();
    }

    private long time;
    private final long tickDelay;
    private final Notyifyer notyifyer;

    public ComputeNodeTimer(long tickDelay, Notyifyer notyifyer) {
        this.tickDelay = tickDelay;
        this.notyifyer = notyifyer;
    }

    public void tick() {
        time++;
        if (time % tickDelay == 0) {
            if (notyifyer != null) {
                notyifyer.notifyListener();
            }

            if (time >= tickDelay * 2) {
                time = 0;
            }
        }
    }
}
