package me.lachlanap.dependencygraph.analyser.java;

import me.lachlanap.dependencygraph.analyser.Analysis;
import me.lachlanap.dependencygraph.analyser.AnalysisBuilder;
import me.lachlanap.dependencygraph.analyser.Entity;

import java.util.Optional;

/**
 * Reads all the bottom-level entities in a project and returns the raw analysis.
 */
public class ProjectAnalyser {

    private final Spider spider;
    private final Loader loader;
    private final Parser parser;
    private final ClassAnalyser classAnalyser;

    public ProjectAnalyser(Spider spider,
                           ThreadSafeLoader loader, Parser parser,
                           ClassAnalyser classAnalyser) {
        this.spider = spider;
        this.loader = loader;
        this.parser = parser;
        this.classAnalyser = classAnalyser;
    }

    public Analysis analyse() {
        AnalysisBuilder classes = spider.findClassesToAnalyse().parallelStream()
                .map(loader::load)
                .map(parser::parse)
                .map(classAnalyser::analyse)
                .reduce(AnalysisBuilder.empty(), AnalysisBuilder::merge);


        AnalysisBuilder rewritten = classes.rewrite(e -> {
            int index = e.getName().indexOf('$');
            if (index >= 0) {
                return e.changeName(e.getName().substring(0, index));
            } else
                return e;
        });

        return rewritten.build();
    }
}
