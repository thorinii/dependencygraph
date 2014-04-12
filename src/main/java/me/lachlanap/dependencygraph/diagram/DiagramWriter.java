package me.lachlanap.dependencygraph.diagram;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Lachlan Phillips
 */
public class DiagramWriter {

    private final Path out;

    public DiagramWriter(Path out) {
        this.out = out;
    }

    public void writeDiagram(String name, Diagram diagram) throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(out.resolve(name)))) {
            writer.println(diagram.buildDiagram());
        }
    }
}
