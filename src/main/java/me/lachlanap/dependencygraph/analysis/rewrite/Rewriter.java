package me.lachlanap.dependencygraph.analysis.rewrite;

import java.util.stream.Collectors;
import me.lachlanap.dependencygraph.analysis.ClassFile;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalysis;

/**
 *
 * @author Lachlan Phillips
 */
public interface Rewriter {

    public String rewriteClassName(String name);

    public default ClassAnalysis rewrite(ClassAnalysis in) {
        return new ClassAnalysis(rewriteClassFile(in.getClassFile()),
                                 in.getDependencies().stream()
                .map(this::rewriteClassName)
                .filter(c -> !c.equals(rewriteClassName(in.getName())))
                .collect(Collectors.toSet()));
    }

    public default ClassFile rewriteClassFile(ClassFile in) {
        return new ClassFile(rewriteClassName(in.getName()),
                             in.getParent(),
                             in.getConstructors(),
                             in.getMethods(),
                             in.getFields());
    }
}