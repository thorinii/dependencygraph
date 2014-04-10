package me.lachlanap.dependencygraph.analysis;

import me.lachlanap.dependencygraph.ClassFile;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ClassAnalyserTest {

    @Test
    public void matchesClassFile() {
        ClassAnalyser analyser = new ClassAnalyser();
        ClassFile classFile = makeClassFile("test.Blank", "test.Parent");

        ClassAnalysis analysis = analyser.analyse(classFile);

        assertThat(analysis.getName(), is("test.Blank"));
        assertThat(analysis.getParent(), is("test.Parent"));
    }

    @Test
    public void capturesParent() {
        ClassAnalyser analyser = new ClassAnalyser();
        ClassFile classFile = makeClassFile("test.Blank", "test.Parent");

        ClassAnalysis analysis = analyser.analyse(classFile);

        assertThat(analysis.getDependencies(), hasItems("test.Parent"));
    }

    private ClassFile makeClassFile(String name, String parent) {
        return new ClassFile(name, parent);
    }
}
