package me.lachlanap.dependencygraph.analyser.java;

import me.lachlanap.dependencygraph.analyser.Analysis;
import me.lachlanap.dependencygraph.analyser.AnalysisBuilder;
import me.lachlanap.dependencygraph.analyser.Rewriter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Reads all the bottom-level entities in a project and returns the raw analysis.
 */
public class ProjectAnalyser {

    public static Analysis analyse(Spider spider,
                                   Loader loader,
                                   Parser parser,
                                   ClassAnalyser classAnalyser,
                                   Rewriter rewriter) {
        AnalysisBuilder entities = spider.findClassesToAnalyse().parallelStream()
                .map(loader::load)
                .map(parser::parse)
                .map(classAnalyser::analyse)
                .reduce(AnalysisBuilder.empty(), AnalysisBuilder::merge);

        AnalysisBuilder rewritten = entities.rewrite(rewriter);

        return rewritten.build();
    }
}
