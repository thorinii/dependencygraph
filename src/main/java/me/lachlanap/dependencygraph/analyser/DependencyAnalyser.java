package me.lachlanap.dependencygraph.analyser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author Lachlan Phillips
 */
public class DependencyAnalyser {
    private final Spider spider;
    private final EntityAnalyser entityAnalyser;
    private final Rewriter rewriter;

    public DependencyAnalyser(Spider spider, EntityAnalyser entityAnalyser, Rewriter rewriter) {
        this.spider = spider;
        this.entityAnalyser = entityAnalyser;
        this.rewriter = rewriter;
    }

    public void analyseAndReport(Log log, Path root, UnaryOperator<Analysis> filter) throws IOException {
        log.info("Analysing");
        Analysis raw = analyse();

        log.info("Filtering");
        raw = filter.apply(raw);

        AnalysisOutput output = AnalysisOutput.createClean(root, raw);

        log.info("Dumping raw analysis");
        output.write("raw.json", new JsonWriter());
        output.write("raw.txt", new TextWriter());

        log.info("Generating diagrams");
        writeDiagrams(output, "all", false);

        output = output.filterAnalysis(Analysis::removeNonProjectDependencies);
        output.filterAnalysis(Analysis::filterRoots).write("roots.txt", new TextEntityWriter());
        writeDiagrams(output, "proj", true);
        writePerParentInOut(output, raw.removeNonProjectDependencies());

        log.info("Done");
    }

    private Analysis analyse() {
        Analysis entities =
                spider.findClassesToAnalyse().parallelStream()
                        .map(entityAnalyser::analyse)
                        .reduce(AnalysisBuilder.empty(), AnalysisBuilder::merge).build();

        return entities.rewrite(rewriter);
    }

    private void writeDiagrams(AnalysisOutput output, String prefix, boolean stripPrefix) throws IOException {
        output.write(prefix + "-classes.dot", new DotWriter(stripPrefix));

        output.filterAnalysis(Analysis::useParent)
                .write(prefix + "-packages.dot", new DotWriter(stripPrefix));
    }

    private void writePerParentInOut(AnalysisOutput output, Analysis raw) throws IOException {
        Map<Entity, Set<Entity>> groups = raw.getEntities().stream().collect(Collectors.groupingBy(Entity::getParent, Collectors.toSet()));

        for (Map.Entry<Entity, Set<Entity>> e : groups.entrySet()) {
            Entity parent = e.getKey();
            Set<Entity> group = e.getValue();

            output.filterAnalysis(analysis -> analysis.filterEntities(group::contains))
                    .write("classes-" + parent.getName() + ".dot", new ClusteringDotWriter(raw, true));
        }
    }
}
