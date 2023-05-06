package hu.vio.thesis.layers.application.refactored;

import org.json.JSONObject;

public class MessageProtocol {

    public class Command {
        public static final String LOG = "LOG";
        public static final String DATA = "DATA";
        public static final String COMMAND = "COMMAND";
        public static final String OTHER = "OTHER";
    }

    private final String layer;
    private String command;
    private String event;
    private Object data;

    public MessageProtocol(String command, String event, Object data) {
        this.layer = "APPLICATION";
        this.command = command;
        this.event = event;
        this.data = data;
    }

    public JSONObject toJSON() {
        JSONObject message = new JSONObject();
        message.put("layer", "APPLICATION");
        message.put("command", command);
        message.put("event", event);
        message.put("message", data);
        return message;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
