package conquest.engine.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

public class InputOutputStream {
    
    private LinkedBlockingQueue<Integer> stream;
    
    private class MyInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            try {
                if (stream.isEmpty()) return -1;
                int read = stream.take();
                return read;
            } catch (InterruptedException e) {
                throw new IOException("Interrupted while waiting on the stream.take().", e);
            }
        }
        
    }
    
    private class MyOutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
            try {
                stream.put(b);
            } catch (InterruptedException e) {
                throw new IOException("Interrupted while waiting on the stream.put(" + b + ").", e);
            }
        }
        
    }
    
    private MyInputStream input;
    private MyOutputStream output;
    
    public InputOutputStream() {
        stream = new LinkedBlockingQueue<Integer>();
        
        input = new MyInputStream();
        output = new MyOutputStream();
    }

    public InputStream getInputStream() {
        return input;
    }

    public OutputStream getOutputStream() {
        return output;
    }
    
}
