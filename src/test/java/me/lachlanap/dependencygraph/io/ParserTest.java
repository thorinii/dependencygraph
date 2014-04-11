package me.lachlanap.dependencygraph.io;

import me.lachlanap.dependencygraph.ClassFile;
import org.junit.Test;

import static me.lachlanap.dependencygraph.Helpers.loadClassFile;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *
 * @author Lachlan Phillips
 */
public class ParserTest {

    @Test
    public void parsesClassName() {
        byte[] bytecode = loadClassFile("UserLostException");
        Parser parser = new Parser();

        ClassFile classFile = parser.parse(bytecode);

        assertThat(classFile.getName(), is("com.planticle.eatit.game.UserLostException"));
    }

    @Test
    public void parsesParent() {
        byte[] bytecode = loadClassFile("UserLostException");
        Parser parser = new Parser();

        ClassFile classFile = parser.parse(bytecode);

        assertThat(classFile.getParent(), is("java.lang.RuntimeException"));
    }

    @Test
    public void parsesBlankConstructor() {
        byte[] bytecode = loadClassFile("UserLostException");
        Parser parser = new Parser();

        ClassFile classFile = parser.parse(bytecode);

        assertThat(classFile.getConstructors().get(0).getArgumentTypes().size(), is(0));
    }

    @Test
    public void parsesParameterisedConstructor() {
        byte[] bytecode = loadClassFile("NameAlreadyTakenException");
        Parser parser = new Parser();

        ClassFile classFile = parser.parse(bytecode);

        assertThat(classFile.getConstructors().get(0).getArgumentTypes(), hasItem("java.lang.String"));
    }

    @Test
    public void parsesParameterisedConstructorWithPrimitives() {
        byte[] bytecode = loadClassFile("Player");
        Parser parser = new Parser();

        ClassFile classFile = parser.parse(bytecode);

        assertThat(classFile.getConstructors().get(0).getArgumentTypes(), hasItem("java.lang.String"));
    }

    @Test
    public void parsesParameterisedMethod() {
        byte[] bytecode = loadClassFile("Player");
        Parser parser = new Parser();

        ClassFile classFile = parser.parse(bytecode);

        assertThat(classFile.getMethod("equals").get().getArgumentTypes(), hasItem("java.lang.Object"));
    }

    @Test
    public void parsesMethodWithReturnType() {
        byte[] bytecode = loadClassFile("Player");
        Parser parser = new Parser();

        ClassFile classFile = parser.parse(bytecode);

        assertThat(classFile.getMethod("getName").get().getReturnType().get(), is("java.lang.String"));
    }

    @Test
    public void parsesCodeWithNew() {
        byte[] bytecode = loadClassFile("NameAlreadyTakenException");
        Parser parser = new Parser();

        ClassFile classFile = parser.parse(bytecode);

        assertThat(classFile.getConstructors().get(0).getCode().getReferencedTypes(), hasItem("java.lang.StringBuilder"));
    }

    @Test
    public void parsesCodeWithInvokeSpecial() {
        byte[] bytecode = loadClassFile("UserLostException");
        Parser parser = new Parser();

        ClassFile classFile = parser.parse(bytecode);

        assertThat(classFile.getConstructors().get(0).getCode().getReferencedTypes(), hasItem("java.lang.String"));
    }

    @Test
    public void parsesField() {
        byte[] bytecode = loadClassFile("Player");
        Parser parser = new Parser();

        ClassFile classFile = parser.parse(bytecode);

        assertThat(classFile.getField("name").get().getType(), is("java.lang.String"));
    }
}
