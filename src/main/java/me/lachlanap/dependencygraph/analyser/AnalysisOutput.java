package me.lachlanap.dependencygraph.analyser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * @author Lachlan Phillips
 */
public class AnalysisOutput {
    private final Path root;
    private final Analysis analysis;

    public AnalysisOutput(Path root, Analysis analysis) {
        this.root = root;
        this.analysis = analysis;
    }

    public void write(String filename, AnalysisWriter writer) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(root.resolve(filename));
             PrintWriter out = new PrintWriter(bw)) {
            writer.write(analysis, out);
        }
    }

    public AnalysisOutput filterAnalysis(UnaryOperator<Analysis> mapper) {
        return new AnalysisOutput(root, mapper.apply(analysis));
    }


    public static AnalysisOutput createClean(Path root, Analysis analysis) {
        createBlankDirectory(root);
        return new AnalysisOutput(root, analysis);
    }

    private static void createBlankDirectory(Path directory) {
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
