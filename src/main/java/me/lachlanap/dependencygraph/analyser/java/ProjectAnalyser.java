package me.lachlanap.dependencygraph.analyser.java;

import java.util.*;
import java.util.stream.Collectors;

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

        String rootPackage;
        if (rootPackageOverride.isPresent())
            rootPackage = rootPackageOverride.get();
        else
            rootPackage = packageAnalyser.findRootPackageFor(packagesAnalysis).orElse("");

        return new ProjectAnalysis(rootPackage,
                                   classesAnalysis, packagesAnalysis);
    }
}
