package hu.vio.thesis.layers.application;

import java.util.HashMap;
import java.util.Map;

public class Args {
    private static Map<String, String> args = new HashMap<>();

    public static void setArgs(String[] args) {
        for (String arg: args) {
            if (arg.contains("=")) {
                String[] pieces = arg.split("=");
                String key = pieces[0];
                String value = pieces[1];
                Args.args.put(key, value);
            } else {
                String key = arg.replace("--", "");
                Args.args.put(key, Boolean.toString(true));
            }
        }
    }

    public static Map<String, String> getArgs() {
        return Args.args;
    }

    public static String get(String key) throws Exception {
        if (Args.args.get(key) == null) {
            throw new Exception();
        }
        return Args.args.get(key);
    }

    public static String get(String key, String defaultValue) {
        return Args.args.get(key) != null ? Args.args.get(key) : defaultValue;
    }
}
