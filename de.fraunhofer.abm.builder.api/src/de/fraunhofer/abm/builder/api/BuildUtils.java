package de.fraunhofer.abm.builder.api;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class BuildUtils {
    /**
     * Creates an error String from a given message and a Throwable
     * @param msg
     * @param e
     * @return
     */
    public static String createErrorString(String msg, Throwable e) {
        StringBuilder sb = new StringBuilder(msg);
        sb.append("\n");
        PrintStream ps;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ps = new PrintStream(bos, true, "utf-8");
            e.printStackTrace(ps);
            sb.append(new String(bos.toByteArray(), "utf-8"));
        } catch (UnsupportedEncodingException e1) {
        }
        return sb.toString();
    }

    public static String toString(ByteArrayOutputStream stream) {
        return new String(stream.toByteArray());
    }
}
