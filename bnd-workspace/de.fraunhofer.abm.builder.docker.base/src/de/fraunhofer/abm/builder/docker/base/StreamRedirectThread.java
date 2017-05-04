package de.fraunhofer.abm.builder.docker.base;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamRedirectThread implements Runnable {

    private static final transient Logger logger = LoggerFactory.getLogger(StreamRedirectThread.class);

    private InputStream in;
    private OutputStream out;

    public StreamRedirectThread(InputStream in, OutputStream out) {
        super();
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            int length = -1;
            byte[] buffer = new byte[1024];
            while(in != null && (length = in.read(buffer)) >= 0) {
                out.write(buffer, 0, length);
            }
        } catch(Exception e) {
            logger.error("Error while redirecting stream", e);
        }
    }
}
