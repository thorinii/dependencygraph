package me.lachlanap.dependencygraph.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Lachlan Phillips
 */
public abstract class AbstractIOLoader implements Loader {

    @Override
    public byte[] load(String path) throws LoadingFailedException {
        try {
            return readFromStream(openStream(path));
        } catch (IOException ioe) {
            throw new LoadingFailedException("Could not read resource", ioe);
        }
    }

    protected abstract InputStream openStream(String path) throws IOException;

    protected byte[] readFromStream(InputStream stream) throws IOException {
        try (InputStream is = stream;
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int read;

            while ((read = is.read(buf)) != -1)
                bos.write(buf, 0, read);

            return bos.toByteArray();
        }
    }
}
