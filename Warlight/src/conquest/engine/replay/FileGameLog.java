package conquest.engine.replay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import conquest.engine.Config;
import conquest.engine.GameResult;

public class FileGameLog extends GameLog {

    private File file;
    
    private List<LogLine> buffer = new ArrayList<LogLine>();

    public static final String REPLAY_END = "---// GAME ENDED //---";

    public FileGameLog(File file) {
        this.file = file;        
    }
    
    @Override
    public synchronized void start(Config config) {
        if (file.exists()) file.delete();
        writeConfig(config);
    }
    
    @Override
    public synchronized void finish(GameResult result) {
        outputBuffer();
        outputResult(result);
    }
        
    @Override
    protected synchronized void log(LogLine line) {
        buffer.add(line);
        if (buffer.size() > 100) {
            outputBuffer();
        }
    }

    private void outputBuffer() {
        PrintWriter writer;
        try {
            if (file.exists()) {
                writer = new PrintWriter(new FileOutputStream(file, true));
            } else {
                writer = new PrintWriter(new FileOutputStream(file));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to log the game into: " + file.getAbsolutePath(), e);
        }
        
        for (LogLine line : buffer) {
            writer.println(line.asString());
        }
        
        writer.flush();
        writer.close();
        
        buffer.clear();
    }
    
    private void writeConfig(Config config) {
        PrintWriter writer;
        try {
            if (file.exists()) {
                writer = new PrintWriter(new FileOutputStream(file, true));
            } else {
                writer = new PrintWriter(new FileOutputStream(file));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to log the game into: " + file.getAbsolutePath(), e);
        }
        
        writer.flush();
        writer.println(config.asString());
        
        writer.close();
    }
    
    private void outputResult(GameResult result) {
        PrintWriter writer;
        try {
            if (file.exists()) {
                writer = new PrintWriter(new FileOutputStream(file, true));
            } else {
                writer = new PrintWriter(new FileOutputStream(file));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to log the game into: " + file.getAbsolutePath(), e);
        }
        
        writer.println(REPLAY_END);
        
        writer.println(result.asString());
        
        writer.println(result.getHumanString());
        
        writer.flush();
        writer.close();
    }


}
