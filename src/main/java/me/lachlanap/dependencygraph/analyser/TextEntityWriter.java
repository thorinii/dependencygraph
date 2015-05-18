package me.lachlanap.dependencygraph.analyser;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Writes the analysis out as a plain-text sorted list of <code>a -> b</code>.
 */
public class TextEntityWriter implements AnalysisWriter {
    @Override
    public void write(Analysis analysis, PrintWriter out) throws IOException {
        analysis.getEntities().stream()
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .forEach(e -> out.println(e.getName()));
    }
}
