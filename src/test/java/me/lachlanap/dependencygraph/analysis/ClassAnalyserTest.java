package me.lachlanap.dependencygraph.analysis;

import java.util.Arrays;
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

    @Test
    public void capturesStaticReferencesInConstructor() {
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

    private ClassFile makeClassFile(String name, String parent) {
        return new ClassFileBuilder(name)
                .setParent(parent)
                .build();
    }
}
