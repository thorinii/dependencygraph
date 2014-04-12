package me.lachlanap.dependencygraph;

import me.lachlanap.dependencygraph.analysis.ClassFile;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalyser;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalysis;
import me.lachlanap.dependencygraph.analysis.*;
import me.lachlanap.dependencygraph.analysis.io.Loader;
import me.lachlanap.dependencygraph.analysis.io.Parser;
import me.lachlanap.dependencygraph.analysis.io.ResourceLoader;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ClassAnalyserIntegrationTest {

    @Test
    public void analysesSimpleExceptionClassCorrectly() {
        String classResource = "UserLostException";
        Loader loader = new ResourceLoader();
        Parser parser = new Parser();
        ClassAnalyser analyser = new ClassAnalyser();

        byte[] bytecode = loader.load(classResource);
        ClassFile classFile = parser.parse(bytecode);
        ClassAnalysis analysis = analyser.analyse(classFile);

        assertThat(analysis.getParent(), is("java.lang.RuntimeException"));
        assertThat(analysis.getDependencies(), hasItems("java.lang.RuntimeException",
                                                        "java.lang.String"));
    }

    @Test
    public void analysesSimpleDataClassCorrectly() {
        String classResource = "Player";
        Loader loader = new ResourceLoader();
        Parser parser = new Parser();
        ClassAnalyser analyser = new ClassAnalyser();

        byte[] bytecode = loader.load(classResource);
        ClassFile classFile = parser.parse(bytecode);
        ClassAnalysis analysis = analyser.analyse(classFile);

        assertThat(analysis.getParent(), is("java.lang.Object"));
        assertThat(analysis.getDependencies(), hasItems("java.lang.String",
                                                        "java.lang.Object",
                                                        "java.lang.Class",
                                                        "java.lang.StringBuilder"));
        assertThat(analysis.getDependencies(), not(hasItem("com.planticle.eatit.game.session.Player")));
    }

}
