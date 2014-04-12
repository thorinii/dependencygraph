package me.lachlanap.dependencygraph.analysis;

import java.util.List;
import java.util.stream.Collectors;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalyser;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalysis;
import me.lachlanap.dependencygraph.analysis.analyser.PackageAnalyser;
import me.lachlanap.dependencygraph.analysis.analyser.PackageAnalysis;
import me.lachlanap.dependencygraph.analysis.io.Loader;
import me.lachlanap.dependencygraph.analysis.io.Parser;
import me.lachlanap.dependencygraph.analysis.io.ThreadSafeLoader;
import me.lachlanap.dependencygraph.analysis.spider.Spider;

/**
 *
 * @author Lachlan Phillips
 */
public class ProjectAnalyser {

    private final Spider spider;
    private final Loader loader;
    private final Parser parser;
    private final ClassAnalyser classAnalyser;
    private final PackageAnalyser packageAnalyser;

    public ProjectAnalyser(Spider spider,
                           ThreadSafeLoader loader, Parser parser,
                           ClassAnalyser classAnalyser, PackageAnalyser packageAnalyser) {
        this.spider = spider;
        this.loader = loader;
        this.parser = parser;
        this.classAnalyser = classAnalyser;
        this.packageAnalyser = packageAnalyser;
    }

    public ProjectAnalysis analyse() {
        List<ClassAnalysis> classesAnalysis = spider.findClassesToAnalyse().parallelStream()
                .map(loader::load)
                .map(parser::parse)
                .map(classAnalyser::analyse)
                .collect(Collectors.toList());
        loader.close();

        List<PackageAnalysis> packagesAnalysis = packageAnalyser.analyse(classesAnalysis);

        return new ProjectAnalysis(classesAnalysis, packagesAnalysis);
    }
}
