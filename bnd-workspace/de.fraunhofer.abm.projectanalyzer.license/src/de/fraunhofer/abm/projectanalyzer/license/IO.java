package de.fraunhofer.abm.projectanalyzer.license;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IO {
    public static String readString(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = in.read(buffer);
        bos.write(buffer, 0, length);
        //        byte[] buffer = new byte[1024];
        //        int length = -1;
        //        while ((length = in.read(buffer)) >= 0) {
        //            bos.write(buffer, 0, length);
        //        }
        return new String(bos.toByteArray());
    }
}
