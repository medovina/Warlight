// Copyright 2014 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//    
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package conquest.engine.io.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import conquest.engine.replay.GameLog;

public class Handler implements IHandler {
    
    String name;
    
    boolean running;
    
    InStream out, err;
    OutStream in;

    private GameLog log = null;

    private int logPlayer;
    
    boolean logToConsole;
    
    public Handler(String handlerName, OutputStream input, boolean inputAutoFlush,
                   InputStream output, InputStream error) throws IOException
    {
        name = handlerName;
        
        in = new OutStream(input, inputAutoFlush);
        
        out = new InStream(handlerName + "-OUT", output);
        
        if (error != null) {
            err = new InStream(handlerName + "-ERR", error);
        }
        
        running = true;
        
        out.start(); 
        
        if (err != null) {
            err.start();
        }
    }
    
    public void setGameLog(GameLog gameLog, int player, boolean logToConsole) {
        this.log = gameLog;
        this.logPlayer = player;
        this.logToConsole = logToConsole;
    }
    
    public void stop()
    {
        try { in.close(); } catch(IOException e) {}

        out.finish(200);
        if (err != null) err.finish(200);
        
        if(out.isAlive())
            out.interrupt();
            
        if (err != null) 
            if(err.isAlive())
                err.interrupt();
        
        try {
            out.join(200);
            if (err != null) err.join(200);
        }
        catch(InterruptedException e) {
        }
    }
    
    public String readLine(long timeOut)
    {
        if (!isRunning()) { return null; }
        try { in.flush(); } catch(IOException e) {}
        String line = out.readLine(timeOut);
        if (line == null)
            System.err.format("ERROR: readLine from %s returned null\n", name);
        
        if (log != null) {
            log.logBotToEngine(logPlayer, line);
        }
        if (logToConsole)
            System.out.println(name + " <-- " + line);
        return line;
    }
    
    public boolean writeLine(String line)
    {
        if(!isRunning()) { return false; }
        try { 
            if (log != null) {
                log.logEngineToBot(logPlayer, line);
            }
            if (logToConsole)
                System.out.println(name + " --> " + line);
            in.writeLine(line.trim());            
            return true;
        } 
        catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        
    }
    
    public boolean isRunning()
    {
        return running;
    }
    
    public String getIn() {
        return in.getData();
    }
    
    public String getOut() {
        return out.getData();
    }
    
    public String getErr() {
        return err == null ? "N/A" : err.getData();
    }
    
}
