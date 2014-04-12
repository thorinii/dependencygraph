package me.lachlanap.dependencygraph;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import me.lachlanap.dependencygraph.analysis.ProjectAnalyser;
import me.lachlanap.dependencygraph.analysis.ProjectAnalysis;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalyser;
import me.lachlanap.dependencygraph.analysis.io.JarLoader;
import me.lachlanap.dependencygraph.analysis.io.Parser;
import me.lachlanap.dependencygraph.analysis.io.ThreadSafeLoader;
import me.lachlanap.dependencygraph.analysis.spider.JarSpider;
import me.lachlanap.dependencygraph.diagram.ClassDiagram;
import me.lachlanap.dependencygraph.diagram.Diagram;
import me.lachlanap.dependencygraph.diagram.DiagramWriter;

public class Main {

    public static void main(String[] args) throws MalformedURLException, IOException {
        URL path = Paths.get("small-jar.jar").toUri().toURL();
        Path out = Paths.get("out");

        ProjectAnalyser analyser = new ProjectAnalyser(
                new JarSpider(path),
                new ThreadSafeLoader(new JarLoader(path)),
                new Parser(),
                new ClassAnalyser());
        ProjectAnalysis analysis = analyser.analyse();

        if (Files.exists(out))
            deleteDirectory(out);
        Files.createDirectories(out);

        Diagram diagram = new ClassDiagram(analysis);
        DiagramWriter writer = new DiagramWriter(out);
        writer.writeDiagram("class.dot", diagram);
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
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete directory '" + directory + "'", ex);
        }
    }
}
