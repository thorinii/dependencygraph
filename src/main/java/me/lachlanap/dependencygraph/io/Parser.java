package me.lachlanap.dependencygraph.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.lachlanap.dependencygraph.ClassFile;
import org.objectweb.asm.*;

/**
 *
 * @author Lachlan Phillips
 */
public class Parser {

    public ClassFile parse(byte[] bytecode) {
        ClassReader reader = new ClassReader(bytecode);

        String className = internalToBinaryClassName(reader.getClassName());
        String parentName = internalToBinaryClassName(reader.getSuperName());
        List<ClassFile.Method> constructors = new ArrayList<>();
        List<ClassFile.Method> methods = new ArrayList<>();

        reader.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (isConstructor(name)) {
                    constructors.add(new ClassFile.Method(
                            "<init>",
                            Arrays.asList(Type.getArgumentTypes(desc)).stream()
                            .filter(t -> isObject(t))
                            .map(t -> t.getInternalName())
                            .map(n -> internalToBinaryClassName(n))
                            .collect(Collectors.toList())));
                } else {
                    methods.add(new ClassFile.Method(
                            name,
                            Arrays.asList(Type.getArgumentTypes(desc)).stream()
                            .filter(t -> isObject(t))
                            .map(t -> t.getInternalName())
                            .map(n -> internalToBinaryClassName(n))
                            .collect(Collectors.toList()),
                            Optional.of(Type.getReturnType(desc))
                            .filter(t -> isObject(t))
                            .map(t -> t.getInternalName())
                            .map(n -> internalToBinaryClassName(n))));
                }

                return null;
            }
        }, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

        return new ClassFile(className, parentName, constructors, methods);
    }

    private String internalToBinaryClassName(String internal) {
        return internal.replaceAll("/", ".");
    }

    private boolean isConstructor(String methodName) {
        return methodName.equals("<init>");
    }

    private boolean isObject(Type t) {
        return t.getSort() == Type.OBJECT;
    }
}
