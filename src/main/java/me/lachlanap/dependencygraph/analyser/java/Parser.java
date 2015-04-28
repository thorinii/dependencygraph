package me.lachlanap.dependencygraph.analyser.java;

import java.util.*;
import java.util.stream.Collectors;

import org.objectweb.asm.*;

/**
 *
 * @author Lachlan Phillips
 */
public class Parser {

    public ClassFile parse(byte[] bytecode) {
        ClassReader reader = new ClassReader(bytecode);

        String className = internalToBinaryClassName(reader.getClassName());
        String parentName;
        if (reader.getSuperName() == null)
            parentName = "java.lang.Object";
        else
            parentName = internalToBinaryClassName(reader.getSuperName());
        List<String> interfaces = getTypes(Arrays.asList(reader.getInterfaces()));

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
                        if (type.startsWith("[")) {
                            Type t = Type.getType(type);
                            if (t.getElementType().getSort() == Type.OBJECT)
                                referencedInCode.add(t.getElementType().getClassName());
                        } else
                            referencedInCode.add(internalToBinaryClassName(type));
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                        if (owner.startsWith("[")) {
                            Type t = Type.getType(owner);
                            if (t.getElementType().getSort() == Type.OBJECT)
                                referencedInCode.add(t.getElementType().getClassName());
                        } else
                            referencedInCode.add(internalToBinaryClassName(owner));

                        getArgumentTypes(desc).forEach(referencedInCode::add);
                        getReturnType(desc).ifPresent(referencedInCode::add);
                    }

                    @Override
                    public void visitEnd() {
                        List<String> exceptionTypes;
                        if (exceptions == null)
                            exceptionTypes = Collections.emptyList();
                        else
                            exceptionTypes = Arrays.asList(exceptions);

                        if (isConstructor(name)) {
                            constructors.add(makeConstructor(desc,
                                                             makeCode(referencedInCode),
                                                             exceptionTypes));
                        } else {
                            methods.add(makeMethod(name,
                                                   desc,
                                                   makeCode(referencedInCode),
                                                   exceptionTypes));
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

        return new ClassFile(className, parentName, interfaces, constructors, methods, fields);
    }

    private String internalToBinaryClassName(String internal) {
        if (internal.charAt(0) == '[')
            throw new IllegalArgumentException("Does not deal with that sort of type: " + internal);
        return internal.replaceAll("/", ".");
    }

    private boolean isConstructor(String methodName) {
        return methodName.equals("<init>");
    }

    private ClassFile.Method makeConstructor(String descriptor, ClassFile.Code code, List<String> exceptions) {
        return new ClassFile.Method("<init>", getArgumentTypes(descriptor), code, getTypes(exceptions));
    }

    private ClassFile.Method makeMethod(String name, String descriptor, ClassFile.Code code, List<String> exceptions) {
        return new ClassFile.Method(name, getArgumentTypes(descriptor), getReturnType(descriptor), code, getTypes(exceptions));
    }

    private List<String> getArgumentTypes(String descriptor) {
        return Arrays.asList(Type.getArgumentTypes(descriptor)).stream()
                .filter(t -> isObject(t))
                .map(t -> t.getInternalName())
                .map(n -> internalToBinaryClassName(n))
                .collect(Collectors.toList());
    }

    private List<String> getTypes(List<String> exceptions) {
        return exceptions.stream()
                .map(e -> Type.getObjectType(e))
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
