package utils;

import java.io.File;

public class Util {
    public static String className(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static File findFile(String path) {
        path = path.replace('/', File.separatorChar);

        String[] places = { ".", "..", "../Warlight" };

        for (String p : places) {
            File file = new File(p, path);
            if (file.exists())
                return file;
        }

        File file = new File(path);
        file = new File(file.getName());
        if (file.exists())
            return file;
        
        throw new RuntimeException("can't find file: " + path);
    }
}
