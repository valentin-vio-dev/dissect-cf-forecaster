package hu.vio.thesis.layers.application;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Config {
    private static class ConfigData {
        public String key;
        public Object value;

        public ConfigData(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    public final Map<String, ConfigData> config;

    public Config() {
        config = new HashMap<>();
    }

    public Config(String json) {
        config = new HashMap<>();
        JSONObject configJSON = new JSONObject(json);
        for (String key: configJSON.keySet()) {
            put(key, configJSON.get(key));
        }
    }

    public Config put(String key, Object value) {
        config.put(key, new ConfigData(key, value));
        return this;
    }

    public void print() {
        Logger.printTitle("Configuration", 25);
        Logger.endl();

        Logger.printTitle("Key", 25);
        Logger.printTitle("Value", 25);
        Logger.endl();
        Logger.line(2, 25);

        for (String key: new TreeSet<>(config.keySet())) {
            Logger.printCell(config.get(key).key, 25);
            Logger.printCell(config.get(key).value.toString(), 25);
            Logger.endl();
        }
    }

    public void saveConfigJSON(String path) {
        JSONObject jsonConfig = new JSONObject();

        for (String key: config.keySet()) {
            JSONObject field = new JSONObject();
            field.put("value", config.get(key).value);
            jsonConfig.put(key, field);
        }

        String jsonString = jsonConfig.toString();

        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.write(jsonString);
            System.out.println("Configuration saved to " + path + "!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveConfigKV(String path) {
        String data = "";
        for (String key: config.keySet()) {
            data += key + "=" + config.get(key).value + "\n";
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.write(data);
            Logger.log("Configuration saved to " + path.replaceAll("\\\\", "/") + "!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        return config.get(key).value.toString();
    }

    public Integer getInteger(String key) {
        return Integer.parseInt(config.get(key).value.toString());
    }

    public Float getFloat(String key) {
        return Float.parseFloat(config.get(key).value.toString());
    }

    public Double getDouble(String key) {
        return Double.parseDouble(config.get(key).value.toString());
    }

    public Boolean getBoolean(String key) {
        return Boolean.parseBoolean(config.get(key).value.toString());
    }

    public Long getLong(String key) {
        return Long.parseLong(config.get(key).value.toString());
    }
}
