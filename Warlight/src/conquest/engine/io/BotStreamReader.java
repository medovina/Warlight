package conquest.engine.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BotStreamReader {

    private InputStreamReader reader;

    private char[] buffer = new char[1];
    
    public BotStreamReader(InputStream input) {
        this.reader = new InputStreamReader(input);
    }
    
    /**
     * Returns next LINE or NULL (in case of stream end).
     * @return
     * @throws IOException
     */
    public String readLine() throws IOException {
        StringBuffer line = new StringBuffer();
        
        while (true) {        
            int read = reader.read(buffer);
            if (read < 0) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // GAME ENDED;
                    return null;
                }
                continue;
            }
            
            if (buffer[0] == '\n') {
                String result = line.toString();
                if (result.endsWith("\r")) result = result.substring(0, result.length()-1);
                return result;
            }
            
            line.append(buffer[0]);        
        }        
    }
    
}
