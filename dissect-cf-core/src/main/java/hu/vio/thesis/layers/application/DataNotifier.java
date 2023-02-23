package hu.vio.thesis;

import java.util.Calendar;

public class DataNotifier {
    public enum DataNotifyType {
        TIME_BASED,
        TICK_COUNT
    }

    private DataNotifyType dataNotifyType;
    private long threshold;
    private IDataListener listener;
    private long currentValue;

    public DataNotifier(DataNotifyType dataNotifyType, long threshold, IDataListener listener) {
        this.dataNotifyType = dataNotifyType;
        this.threshold = threshold;
        this.listener = listener;
        this.currentValue = dataNotifyType == DataNotifyType.TICK_COUNT ? 0 : Calendar.getInstance().getTimeInMillis(); // TODO time
    }

    public void tick() {
        if (dataNotifyType == DataNotifyType.TIME_BASED) {
            // Todo
        } else if (dataNotifyType == DataNotifyType.TICK_COUNT) {
            currentValue++;

            if (currentValue >= threshold) {
                listener.notifyDataListener();
                currentValue = 0;
            }
        }
    }
}
