package me.lachlanap.dependencygraph.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Lachlan Phillips
 */
public class ResourceLoader implements Loader {

    @Override
    public byte[] load(String path) {
        return load(getClass().getResourceAsStream(path));
    }

    private byte[] load(InputStream stream) {
        try (InputStream is = stream;
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
