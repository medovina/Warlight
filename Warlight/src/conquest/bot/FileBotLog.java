package conquest.bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FileBotLog {

    private File file;
    
    public FileBotLog(File file) {
        this.file = file;        
    }
    
    public void start() {
        if (file.exists()) file.delete();
    }
    
    public void finish() {        
    }
    
    public synchronized void log(String msg) {
        PrintWriter writer;
        
        try {
            if (file.exists()) {
                writer = new PrintWriter(new FileOutputStream(file, true));
            } else {
                writer = new PrintWriter(new FileOutputStream(file));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to log the bot into: " + file.getAbsolutePath(), e);
        }
        
        writer.println(msg);
        writer.flush();
        
        writer.close();
    }

}
