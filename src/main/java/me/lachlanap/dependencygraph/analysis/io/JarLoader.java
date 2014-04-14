package me.lachlanap.dependencygraph.analysis.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 *
 * @author Lachlan Phillips
 */
public class JarLoader extends AbstractIOLoader {

    private final Path jar;
    private final Map<String, byte[]> cache;

    private JarInputStream stream;
    private boolean finishedReading;

    public JarLoader(Path jar) {
        this.jar = jar;
        cache = new HashMap<>();

        stream = null;
        finishedReading = false;
    }

    @Override
    public byte[] safeLoad(String path) throws IOException, LoadingFailedException {
        if (cache.containsKey(path))
            return cache.get(path);
        else {
            byte[] data = readFromStream(openStreamTo(path));
            cache.put(path, data);
            return data;
        }
    }

    private InputStream openStreamTo(String path) throws IOException {
        if (finishedReading)
            throw new LoaderCouldNotFindClassException(path);
        else {
            if (stream == null)
                setupStream();

            JarEntry entry;

            while ((entry = stream.getNextJarEntry()) != null) {
                String fileName = entry.getName();
                if (isClass(fileName)) {
                    String className = pathToClass(fileName);
                    if (className.equals(path))
                        return stream;
                    else
                        cache.put(className, readFromStream(stream));
                }
            }

            finishedReading = true;

            throw new LoaderCouldNotFindClassException(path);
        }
    }

    private void setupStream() {
        try {
            stream = new JarInputStream(Files.newInputStream(jar));
        } catch (IOException ioe) {
            throw new LoadingFailedException("Could not open jar " + jar, ioe);
        }
    }

    private boolean isClass(String fileName) {
        return fileName.endsWith(".class");
    }

    private String pathToClass(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf(".class"))
                .replaceAll("/", ".");
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
            finishedReading = true;
            stream.close();
        } catch (IOException ex) {
            throw new RuntimeException("Could not close stream", ex);
        }
    }
}
