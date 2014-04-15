package me.lachlanap.dependencygraph.diagram;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

    public void writeDiagram(String name, Diagram diagram) throws DiagramWritingException {
        String result = generateDiagram(diagram);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(out.resolve(name))),
                                                        result.length())) {
            writer.write(result, 0, result.length());
        } catch (IOException ioe) {
            throw new DiagramWritingException("Failed to write diagram", ioe);
        }
    }

    private String generateDiagram(Diagram diagram) {
        return diagram.buildDiagram();
    }
}
