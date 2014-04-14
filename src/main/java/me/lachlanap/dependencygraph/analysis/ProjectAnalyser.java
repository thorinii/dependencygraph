package me.lachlanap.dependencygraph.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalyser;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalysis;
import me.lachlanap.dependencygraph.analysis.analyser.PackageAnalyser;
import me.lachlanap.dependencygraph.analysis.analyser.PackageAnalysis;
import me.lachlanap.dependencygraph.analysis.io.Loader;
import me.lachlanap.dependencygraph.analysis.io.Parser;
import me.lachlanap.dependencygraph.analysis.io.ThreadSafeLoader;
import me.lachlanap.dependencygraph.analysis.rewrite.Rewriter;
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
    private final Rewriter rewriter;

    public ProjectAnalyser(Spider spider,
                           ThreadSafeLoader loader, Parser parser,
                           ClassAnalyser classAnalyser, PackageAnalyser packageAnalyser,
                           Rewriter rewriter) {
        this.spider = spider;
        this.loader = loader;
        this.parser = parser;
        this.classAnalyser = classAnalyser;
        this.packageAnalyser = packageAnalyser;
        this.rewriter = rewriter;
    }

    public ProjectAnalysis analyse() {
        Map<String, ClassAnalysis> classesAnalysisByName = spider.findClassesToAnalyse().parallelStream()
                .map(loader::load)
                .map(parser::parse)
                .map(classAnalyser::analyse)
                .map(rewriter::rewrite)
                .collect(Collectors.groupingBy(ClassAnalysis::getName,
                                               Collectors.reducing(null, (a, b) -> {
                                                   if (a == null)
                                                       return b;
                                                   else if (b == null)
                                                       return a;
                                                   else {
                                                       Set<String> combined = new HashSet<>(a.getDependencies());
                                                       combined.addAll(b.getDependencies());
                                                       return new ClassAnalysis(a.getClassFile(), combined);
                                                   }
                                               })));

        List<ClassAnalysis> classesAnalysis = classesAnalysisByName.values().stream().collect(Collectors.toList());
        loader.close();

        List<PackageAnalysis> packagesAnalysis = packageAnalyser.analyse(classesAnalysis);

        String rootPackage = packageAnalyser.findRootPackageFor(packagesAnalysis).orElse("");

        return new ProjectAnalysis(rootPackage,
                                   classesAnalysis, packagesAnalysis);
    }
}
