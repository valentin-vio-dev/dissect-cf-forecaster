package hu.vio.thesis.layers.application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final public class Logger {
    static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static void log(String text) {
        String message = "[" + DTF.format(LocalDateTime.now()) + "]\t" + text;
        System.out.println(message);
    }

    public static void log(String... texts) {
        StringBuilder msg = new StringBuilder();
        for (String text: texts) {
            msg.append(text).append("\t");
        }
        System.out.println("[" + DTF.format(LocalDateTime.now()) + "]\t" + msg);
    }

    public static void printCell(String text, int length) {
        if (text.length() >= length) {
            String tmp = text.substring(0, length);
            System.out.print(tmp);
        } else {
            int diff = length - text.length();
            StringBuilder tmp = new StringBuilder(text);
            for (int i=0; i<diff; i++) {
                tmp.append(" ");
            }
            System.out.print(tmp);
        }
    }

    public static void printTitle(String text, int length) {
        text = "[" + text + "]";
        if (text.length() >= length) {
            String tmp = text.substring(0, length);
            System.out.print(tmp);
        } else {
            int diff = length - text.length();
            StringBuilder tmp = new StringBuilder(text);
            for (int i=0; i<diff; i++) {
                tmp.append(" ");
            }
            System.out.print(tmp);
        }
    }

    public static void endl() {
        System.out.print("\n");
    }

    public static void line(int numOfEntities, int length) {
        StringBuilder builder = new StringBuilder("");
        for (int i=0; i<numOfEntities; i++) {
            for (int j=0; j<length; j++) {
                builder.append(".");
            }
        }
        System.out.println(builder);
    }

    public static void emptyLine(int numOfentities, int length) {
        StringBuilder builder = new StringBuilder("");
        for (int i=0; i<numOfentities; i++) {
            for (int j=0; j<length; j++) {
                builder.append(" ");
            }
        }
        System.out.println(builder);
    }

    public static void section(int numOfentities, int length) {
        StringBuilder builder = new StringBuilder("");
        for (int i=0; i<numOfentities; i++) {
            for (int j=0; j<length; j++) {
                builder.append("*");
            }
        }
        System.out.println(builder);
    }
}
