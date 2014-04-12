package me.lachlanap.dependencygraph.analysis.analyser;

import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalyser;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalysis;
import java.util.Arrays;
import me.lachlanap.dependencygraph.analysis.ClassFile;
import me.lachlanap.dependencygraph.analysis.ClassFileBuilder;
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

    @Test
    public void capturesStaticReferencesInAConstructor() {
        ClassAnalyser analyser = new ClassAnalyser();
        ClassFile classFile = new ClassFileBuilder("test.Blank")
                .appendConstructor(Arrays.asList("test.InjectedClass"))
                .build();

        ClassAnalysis analysis = analyser.analyse(classFile);

        assertThat(analysis.getDependencies(), hasItems("test.InjectedClass"));
    }

    @Test
    public void capturesStaticReferencesInAMethod() {
        ClassAnalyser analyser = new ClassAnalyser();
        ClassFile classFile = new ClassFileBuilder("test.Blank")
                .appendMethod("method", "test.ReturnClass", Arrays.asList("test.ArgumentClass"))
                .build();

        ClassAnalysis analysis = analyser.analyse(classFile);

        assertThat(analysis.getDependencies(), hasItems("test.ArgumentClass", "test.ReturnClass"));
    }

    @Test
    public void capturesStaticReferencesInAField() {
        ClassAnalyser analyser = new ClassAnalyser();
        ClassFile classFile = new ClassFileBuilder("test.Blank")
                .appendField("method", "test.ValueClass")
                .build();

        ClassAnalysis analysis = analyser.analyse(classFile);

        assertThat(analysis.getDependencies(), hasItems("test.ValueClass"));
    }

    @Test
    public void capturesExecutableReferencesInAConstructor() {
        ClassAnalyser analyser = new ClassAnalyser();
        ClassFile classFile = new ClassFileBuilder("test.Blank")
                .appendConstructorWithCode(Arrays.asList("test.ReferencedInCode"))
                .build();

        ClassAnalysis analysis = analyser.analyse(classFile);

        assertThat(analysis.getDependencies(), hasItems("test.ReferencedInCode"));
    }

    @Test
    public void capturesExecutableReferencesInAMethod() {
        ClassAnalyser analyser = new ClassAnalyser();
        ClassFile classFile = new ClassFileBuilder("test.Blank")
                .appendMethodWithCode("method", Arrays.asList("test.ReferencedInCode"))
                .build();

        ClassAnalysis analysis = analyser.analyse(classFile);

        assertThat(analysis.getDependencies(), hasItems("test.ReferencedInCode"));
    }

    @Test
    public void doesNotReferenceSelf() {
        ClassAnalyser analyser = new ClassAnalyser();
        ClassFile classFile = new ClassFileBuilder("test.Blank")
                .appendMethodWithCode("method", Arrays.asList("test.ReferencedInCode", "test.Blank"))
                .build();

        ClassAnalysis analysis = analyser.analyse(classFile);

        assertThat(analysis.getDependencies(), not(hasItem("test.Blank")));
    }

    private ClassFile makeClassFile(String name, String parent) {
        return new ClassFileBuilder(name)
                .setParent(parent)
                .build();
    }
}
