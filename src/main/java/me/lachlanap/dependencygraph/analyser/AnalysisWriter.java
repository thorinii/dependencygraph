package me.lachlanap.dependencygraph.analyser;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Supports writing the analysis out in some shape or format.
 */
public interface AnalysisWriter {
    public void write(Analysis a, PrintWriter writer) throws IOException;
}
