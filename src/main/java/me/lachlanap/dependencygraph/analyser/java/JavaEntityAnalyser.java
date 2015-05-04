package me.lachlanap.dependencygraph.analyser.java;

import me.lachlanap.dependencygraph.analyser.AnalysisBuilder;
import me.lachlanap.dependencygraph.analyser.EntityAnalyser;
import me.lachlanap.dependencygraph.analyser.ParsingException;

/**
 * Analyses Java classes by loading them, parsing them, and analysing the bytecode.
 */
public class JavaEntityAnalyser implements EntityAnalyser {
    private final Loader loader;
    private final Parser parser;
    private final ClassAnalyser classAnalyser;

    public JavaEntityAnalyser(Loader loader) {
        this.loader = loader;
        this.parser = new Parser();
        this.classAnalyser = new ClassAnalyser();
    }

    @Override
    public AnalysisBuilder analyse(String name) throws ParsingException {
        return classAnalyser.analyse(parser.parse(loader.load(name)));
    }
}
