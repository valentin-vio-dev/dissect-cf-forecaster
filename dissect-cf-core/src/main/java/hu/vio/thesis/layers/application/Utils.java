package hu.vio.thesis.layers.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;

public class Utils {

    public static void createDirectory(String path) {
        Path newDir = Paths.get(path);

        try {
            Files.createDirectory(newDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String makeUniformIDString(String text) {
        return stripAccents(text.replaceAll(" ", "_").replaceAll("\\.", "_").toUpperCase());
    }

    public static String strToStrSpace(String str, int maxSize) {
        return str.length() > maxSize ? str.substring(0, maxSize) + "..." : str;
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }
}
