package me.lachlanap.dependencygraph.io;

import me.lachlanap.dependencygraph.ClassFile;
import org.objectweb.asm.ClassReader;

/**
 *
 * @author Lachlan Phillips
 */
public class Parser {

    public ClassFile parse(byte[] bytecode) {
        ClassReader reader = new ClassReader(bytecode);

        String className = reader.getClassName()
                .replaceAll("/", ".");
        String parentName = reader.getSuperName()
                .replaceAll("/", ".");

        return new ClassFile(className, parentName);
    }
}
