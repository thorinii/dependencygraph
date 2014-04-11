package me.lachlanap.dependencygraph.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 *
 * @author Lachlan Phillips
 */
public class JarLoader extends AbstractIOLoader {

    private final URL jarUrl;
    private final Map<String, byte[]> cache;

    private JarInputStream stream;

    public JarLoader(URL url) {
        jarUrl = url;
        cache = new HashMap<>();

        stream = null;
    }

    @Override
    public byte[] load(String path) throws LoadingFailedException {
        if (cache.containsKey(path))
            return cache.get(path);
        else {
            byte[] data = super.load(path);
            cache.put(path, data);
            return data;
        }
    }

    @Override
    protected InputStream openStream(String path) throws IOException {
        if (stream == null)
            setupStream();

        JarEntry entry;

        while ((entry = stream.getNextJarEntry()) != null) {
            if (entry.getName().equals(classToPath(path)))
                return stream;
        }

        throw new LoaderCouldNotFindClassException(path);
    }

    private void setupStream() {
        try {
            stream = new JarInputStream(jarUrl.openStream());
        } catch (IOException ioe) {
            throw new LoadingFailedException("Could not open jar " + jarUrl, ioe);
        }
    }

    private String classToPath(String className) {
        return className.replaceAll("\\.", "/") + ".class";
    }

    @Override
    protected byte[] readFromStream(InputStream stream) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int read;

            while ((read = stream.read(buf)) != -1)
                bos.write(buf, 0, read);

            return bos.toByteArray();
        }
    }

    @Override
    public void close() {
        try {
            stream.close();
        } catch (IOException ex) {
            throw new RuntimeException("Could not close stream", ex);
        }
    }
}
