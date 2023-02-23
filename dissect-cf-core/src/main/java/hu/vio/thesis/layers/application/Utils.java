package hu.vio.thesis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    public static String getRoot() {
        return System.getProperty("user.dir");
    }

    public static void cleanAndCreateDirectory(String path) {
        deleteDir(new File(path));
        Path newDir= Paths.get(path);

        try {
            Files.createDirectory(newDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    public static String makeUniformIDString(String text) {
        return text.replaceAll(" ", "_").replaceAll("\\.", "_").toUpperCase();
    }
}
