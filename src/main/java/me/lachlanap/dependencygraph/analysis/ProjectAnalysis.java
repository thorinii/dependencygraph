package me.lachlanap.dependencygraph.analysis;

import java.util.List;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalysis;

/**
 *
 * @author Lachlan Phillips
 */
public class ProjectAnalysis {

    private final List<ClassAnalysis> classesAnalysis;

    public ProjectAnalysis(List<ClassAnalysis> classesAnalysis) {
        this.classesAnalysis = classesAnalysis;
    }

    public List<ClassAnalysis> getClassAnalysis() {
        return classesAnalysis;
    }
}
