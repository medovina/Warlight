package conquest.engine.replay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import conquest.engine.Config;
import conquest.engine.io.handler.IHandler;
import conquest.engine.replay.GameLog.LogLine;
import conquest.engine.replay.GameLog.Way;

public class ReplayHandler implements IHandler {

    private Config config;
    
    private List<LogLine> log = new LinkedList<LogLine>();
    
    private int lineNum = 1;
    
    private boolean running = true;
    
    public ReplayHandler(File file) {
        readFile(file);
    }
    
    private void readFile(File file) {
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new FileReader(file));
            config = Config.fromString(reader.readLine());
            
            while (true) {
                String line = reader.readLine();
                if (line.trim().length() == 0) continue;
                if (line.equals(FileGameLog.REPLAY_END)) break;
                log.add(LogLine.fromString(line));
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to read replay from: " + file.getAbsolutePath(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    public Config getConfig() {
        return config;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void stop() {
        running = false;
        log.clear();
    }
    
    private LogLine getNextLogLine() {
        LogLine logLine;
        while (true) {
            if (log.size() == 0) {
                throw new RuntimeException("Log finished.");
            }
            logLine = log.remove(0);
            if (!logLine.comment) break;
        }
        return logLine;
    }

    @Override
    public String readLine(long timeOut) {
        if (!running) {
            throw new RuntimeException("Not running.");
        }

        LogLine logLine = getNextLogLine();
        
        if (logLine.way != Way.BOT_TO_ENGINE) {
            throw new RuntimeException("Line " + lineNum + " is not BOT->ENGINE: " + logLine.asString());
        }
        ++lineNum;
        
        System.out.println(logLine.player + " <-- " + logLine.line);
        
        return logLine.line;
    }

    @Override
    public boolean writeLine(String line) {
        if (!running) {
            throw new RuntimeException("Not running.");
        }
        
        LogLine logLine = getNextLogLine();
        
        if (logLine.way != Way.ENGINE_TO_BOT) {
            throw new RuntimeException("Line " + lineNum + " is not ENGINE->BOT: " + logLine.asString());
        }
        if (!line.equals(logLine.line)) {
            throw new RuntimeException("Received invalid request from the engine for line " + lineNum + ":\n" + line + "\nExpected:\n" + logLine.line);
        }
        ++lineNum;
        
        System.out.println(logLine.player + " --> " + logLine.line);
        
        return true;
    }

    @Override
    public void setGameLog(GameLog log, int player, boolean logToConsole) {
    }

}
