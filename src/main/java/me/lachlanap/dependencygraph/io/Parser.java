package me.lachlanap.dependencygraph.io;

import java.util.*;
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
        List<ClassFile.Field> fields = new ArrayList<>();

        reader.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                Set<String> referencedInCode = new HashSet<>();

                return new MethodVisitor(Opcodes.ASM5) {

                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        referencedInCode.add(internalToBinaryClassName(type));
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                        referencedInCode.add(internalToBinaryClassName(owner));
                        getArgumentTypes(desc).forEach(referencedInCode::add);
                        getReturnType(desc).ifPresent(referencedInCode::add);
                    }

                    @Override
                    public void visitEnd() {
                        if (isConstructor(name)) {
                            constructors.add(makeConstructor(desc, makeCode(referencedInCode)));
                        } else {
                            methods.add(makeMethod(name, desc, makeCode(referencedInCode)));
                        }
                    }
                };
            }

            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                return new FieldVisitor(Opcodes.ASM5) {
                    @Override
                    public void visitEnd() {
                        makeField(name, desc).ifPresent(fields::add);
                    }
                };
            }
        }, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

        return new ClassFile(className, parentName, constructors, methods, fields);
    }

    private String internalToBinaryClassName(String internal) {
        return internal.replaceAll("/", ".");
    }

    private boolean isConstructor(String methodName) {
        return methodName.equals("<init>");
    }

    private ClassFile.Method makeConstructor(String descriptor, ClassFile.Code code) {
        return new ClassFile.Method("<init>", getArgumentTypes(descriptor), code);
    }

    private ClassFile.Method makeMethod(String name, String descriptor, ClassFile.Code code) {
        return new ClassFile.Method(name, getArgumentTypes(descriptor), getReturnType(descriptor), code);
    }

    private List<String> getArgumentTypes(String descriptor) {
        return Arrays.asList(Type.getArgumentTypes(descriptor)).stream()
                .filter(t -> isObject(t))
                .map(t -> t.getInternalName())
                .map(n -> internalToBinaryClassName(n))
                .collect(Collectors.toList());
    }

    private Optional<String> getReturnType(String descriptor) {
        return Optional.of(Type.getReturnType(descriptor))
                .filter(t -> isObject(t))
                .map(t -> t.getInternalName())
                .map(n -> internalToBinaryClassName(n));
    }

    private ClassFile.Code makeCode(Set<String> referencedInCode) {
        return new ClassFile.Code(referencedInCode);
    }

    private Optional<ClassFile.Field> makeField(String name, String descriptor) {
        return Optional.of(Type.getType(descriptor))
                .filter(t -> isObject(t))
                .map(t -> t.getInternalName())
                .map(n -> internalToBinaryClassName(n))
                .map(n -> new ClassFile.Field(name, n));
    }

    private boolean isObject(Type t) {
        return t.getSort() == Type.OBJECT;
    }
}
