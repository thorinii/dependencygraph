package me.lachlanap.dependencygraph;

import me.lachlanap.dependencygraph.analysis.*;
import me.lachlanap.dependencygraph.io.Loader;
import me.lachlanap.dependencygraph.io.Parser;
import me.lachlanap.dependencygraph.io.ResourceLoader;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ClassAnalyserIntegrationTest {

    @Test
    public void analysesSimpleExceptionClassCorrectly() {
        String classResource = "/UserLostException.class";
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

}
