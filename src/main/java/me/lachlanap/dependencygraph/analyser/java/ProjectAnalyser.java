package me.lachlanap.dependencygraph.analyser.java;

import me.lachlanap.dependencygraph.analyser.Analysis;
import me.lachlanap.dependencygraph.analyser.AnalysisBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Reads all the bottom-level entities in a project and returns the raw analysis.
 */
public class ProjectAnalyser {

    private final Spider spider;
    private final Loader loader;
    private final Parser parser;
    private final ClassAnalyser classAnalyser;
    private final PackageAnalyser packageAnalyser;
    private final Rewriter rewriter;
    private final Optional<String> rootPackageOverride;

    public ProjectAnalyser(Spider spider,
                           ThreadSafeLoader loader, Parser parser,
                           ClassAnalyser classAnalyser, PackageAnalyser packageAnalyser,
                           Rewriter rewriter,
                           Optional<String> rootPackageOverride) {
        this.spider = spider;
        this.loader = loader;
        this.parser = parser;
        this.classAnalyser = classAnalyser;
        this.packageAnalyser = packageAnalyser;
        this.rewriter = rewriter;
        this.rootPackageOverride = rootPackageOverride;
    }

    public Analysis analyse() {
        AnalysisBuilder classes = spider.findClassesToAnalyse().parallelStream()
                .map(loader::load)
                .map(parser::parse)
                .map(classAnalyser::analyse)
                // TODO: map(rewriter::rewrite)
                .reduce(AnalysisBuilder.empty(), AnalysisBuilder::merge);

        return classes.build();
    }
}
