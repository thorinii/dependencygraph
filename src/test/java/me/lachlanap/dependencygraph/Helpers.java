package me.lachlanap.dependencygraph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Lachlan Phillips
 */
public class Helpers {

    public static byte[] loadClassFile(String resource) {
        try (InputStream is = Helpers.class.getResourceAsStream(resource);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int read;
            while ((read = is.read(buf)) != -1)
                bos.write(buf, 0, read);
            return bos.toByteArray();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
