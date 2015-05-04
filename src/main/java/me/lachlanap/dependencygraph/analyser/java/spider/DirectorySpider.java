package me.lachlanap.dependencygraph.analyser.java.spider;

import me.lachlanap.dependencygraph.analyser.Spider;
import me.lachlanap.dependencygraph.analyser.java.SpiderException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Lachlan Phillips
 */
public class DirectorySpider implements Spider {

    private final Path directory;

    public DirectorySpider(Path directory) {
        this.directory = directory;
    }

    @Override
    public List<String> findClassesToAnalyse() throws SpiderException {
        try {
            return Files.walk(directory)
                    .filter((Path p) -> !Files.isDirectory(p))
                    .map((Path f) -> directory.relativize(f))
                    .map((Path f) -> f.toString())
                    .filter((String f) -> f.endsWith(".class"))
                    .map((String f) -> f.substring(0, f.lastIndexOf(".class")).replaceAll("/", "."))
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            throw new SpiderException("Failed to walk directory tree", ioe);
        }
    }
}
