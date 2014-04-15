package me.lachlanap.dependencygraph.analysis.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Lachlan Phillips
 */
public class DirectoryLoader extends AbstractIOLoader implements ThreadSafeLoader {

    private final Path directory;

    public DirectoryLoader(Path directory) {
        this.directory = directory;
    }

    @Override
    protected byte[] safeLoad(String path) throws LoadingFailedException, IOException {
        Path classFile = classNameToPath(path);

        if (Files.notExists(classFile))
            throw new LoaderCouldNotFindClassException(path);

        try (InputStream stream = Files.newInputStream(classFile)) {
            return readFromStream(stream);
        }
    }

    private Path classNameToPath(String name) {
        return directory.resolve(name.replaceAll("\\.", "/") + ".class");
    }
}
