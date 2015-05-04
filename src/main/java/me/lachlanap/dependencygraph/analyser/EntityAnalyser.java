package me.lachlanap.dependencygraph.analyser;

/**
 * Analyses an Entity by name.
 */
public interface EntityAnalyser {
    public AnalysisBuilder analyse(String name) throws ParsingException;
}
