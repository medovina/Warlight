package utils;

public class Util {
    public static String className(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }
}
