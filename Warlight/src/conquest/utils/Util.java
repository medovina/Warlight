package conquest.utils;

import java.io.File;

public class Util {
    public static File findFile(String path) {
        path = path.replace('/', File.separatorChar);

        File file = new File(path);
        if (file.exists())
            return file;
        
        file = new File("..", path);
        if (file.exists())
            return file;

        throw new RuntimeException("can't find file: " + path);
    }
}
