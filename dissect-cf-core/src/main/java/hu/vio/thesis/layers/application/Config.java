package hu.vio.thesis;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static class ConfigData {
        public String key;
        public Object value;
        public Object type;

        public ConfigData(String key, Object value, Object type) {
            this.key = key;
            this.value = value;
            this.type = type;
        }
    }

    public final Map<String, ConfigData> config;

    public Config() {
        config = new HashMap<>();
    }

    public Config put(String key, Object value) {
        config.put(key, new ConfigData(key, value, value.getClass().getSimpleName()));
        return this;
    }

    public void print() {
        Logger.printTitle("Configuration", 25);
        Logger.endl();

        Logger.printTitle("Key", 25);
        Logger.printTitle("Value", 25);
        Logger.printTitle("Type", 25);
        Logger.endl();
        Logger.line(3, 25);

        for (String key: config.keySet()) {
            Logger.printCell(config.get(key).key, 25);
            Logger.printCell(config.get(key).value.toString(), 25);
            Logger.printCell(config.get(key).type.toString(), 25);
            Logger.endl();
        }

        /*try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public void saveConfig(String file) {
        JSONObject jsonConfig = new JSONObject();

        for (String key: config.keySet()) {
            JSONObject field = new JSONObject();
            field.put("value", config.get(key).value);
            field.put("type", config.get(key).type);
            jsonConfig.put(key, field);
        }

        String jsonString = jsonConfig.toString();

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.write(jsonString);
            System.out.println("Configuration saved to " + file + "!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(config.get(key).value);
    }
}
