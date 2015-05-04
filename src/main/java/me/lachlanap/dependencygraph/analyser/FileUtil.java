package me.lachlanap.dependencygraph.analyser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author Lachlan Phillips
 */
public class FileUtil {

    public static void createBlankDirectory(Path directory) {
        if (Files.exists(directory))
            deleteDirectory(directory);

        try {
            Files.createDirectories(directory);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create directory '" + directory + "'", ex);
        }
    }

    private static void deleteDirectory(Path directory) {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (!Files.isSameFile(dir, directory))
                        Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete directory '" + directory + "'", ex);
        }
    }
}
