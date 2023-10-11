package reflection.hacks.internal.injector;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;
import reflection.hacks.api.reflect.Classes;
import reflection.hacks.internal.access.JavaLangAccessBridge;
import reflection.hacks.internal.util.function.ThrowingExecutable;

/**
 * Helper class that generates an {@code Injected} class inside the given package.
 * <p>
 * The source of the generated class is as follows:
 * <blockquote><pre>{@code
 * public class Injector {
 *     static {
 *         final Class<?> injectorClass = Injector.class;
 *         final Class<?> loaderClass = injectorClass.getClassLoader().getClass();
 *         final Module javaBaseModule = Object.class.getModule();
 *         final Module loaderModule = loaderClass.getModule();
 *         // Obtain the jdk.internal.access.JavaLangAccess instance via jdk.internal.access.SharedSecrets
 *         final jdk.internal.access.JavaLangAccess javaLangAccess = jdk.internal.access.SharedSecrets.getJavaLangAccess();
 *
 *         // Export the jdk.internal.access package to the loader module to enable jdk.internal.access.JavaLangAccess access
 *         javaLangAccess.addExports(javaBaseModule, "jdk.internal.access", loaderModule);
 *     }
 * }
 * }</pre></blockquote>
 * <p>
 * In conjunction with {@link Loader}, this is used to define and load a class inside a proxy module which has access to the {@link jdk.internal.access} package.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @see JavaLangAccessBridge#gainInternalAccess()
 * @since 1.0
 */
public final class Injector {

    /**
     * The name of the generated "injected" class
     */
    private static final String INJECTED_CLASS_NAME = "Injected";

    /**
     * Generates an "injected" class inside the package with the given name.
     *
     * @param packageName The name of the package the class should be generated in
     * @return the bytes of the generated injected class
     */
    public static byte @NotNull [] generateIn(final @NotNull String packageName) {
        final String fullInjectedClassName = packageName + "/" + Injector.INJECTED_CLASS_NAME;
        final String injectorClassDescriptor = "L" + fullInjectedClassName + ";";

        final ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor methodVisitor;

        classWriter.visit(
                Opcodes.V17,
                Opcodes.ACC_SUPER,
                fullInjectedClassName,
                null,
                "java/lang/Object",
                null
        );

        // Default constructor bytecode (<init>()V)
        {
            methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    "<init>",
                    "()V",
                    null,
                    null
            );

            methodVisitor.visitCode();

            final Label l0 = new Label();
            methodVisitor.visitLabel(l0);

            methodVisitor.visitVarInsn(
                    Opcodes.ALOAD,
                    0
            );
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    "java/lang/Object",
                    "<init>",
                    "()V",
                    false
            );
            methodVisitor.visitInsn(
                    Opcodes.RETURN
            );

            final Label l1 = new Label();
            methodVisitor.visitLabel(l1);

            methodVisitor.visitLocalVariable(
                    "this",
                    injectorClassDescriptor,
                    null,
                    l0,
                    l1,
                    0
            );

            methodVisitor.visitMaxs(1, 1);

            methodVisitor.visitEnd();
        }

        // Static class initializer bytecode (<clinit>()V)
        {
            methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_STATIC,
                    "<clinit>",
                    "()V",
                    null,
                    null
            );

            methodVisitor.visitCode();

            final Label l0 = new Label();
            methodVisitor.visitLabel(l0);

            methodVisitor.visitLdcInsn(
                    Type.getType(injectorClassDescriptor)
            );
            methodVisitor.visitVarInsn(
                    Opcodes.ASTORE,
                    0
            );

            final Label l1 = new Label();
            methodVisitor.visitLabel(l1);

            methodVisitor.visitVarInsn(
                    Opcodes.ALOAD,
                    0
            );
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Class",
                    "getClassLoader",
                    "()Ljava/lang/ClassLoader;",
                    false
            );
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Object",
                    "getClass",
                    "()Ljava/lang/Class;",
                    false
            );
            methodVisitor.visitVarInsn(
                    Opcodes.ASTORE, 1);

            final Label l2 = new Label();
            methodVisitor.visitLabel(l2);

            methodVisitor.visitLdcInsn(
                    Type.getType("Ljava/lang/Object;")
            );
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Class",
                    "getModule",
                    "()Ljava/lang/Module;",
                    false
            );
            methodVisitor.visitVarInsn(
                    Opcodes.ASTORE,
                    2
            );

            final Label l3 = new Label();
            methodVisitor.visitLabel(l3);

            methodVisitor.visitVarInsn(
                    Opcodes.ALOAD,
                    1
            );
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Class",
                    "getModule",
                    "()Ljava/lang/Module;",
                    false
            );
            methodVisitor.visitVarInsn(
                    Opcodes.ASTORE,
                    3
            );

            final Label l4 = new Label();
            methodVisitor.visitLabel(l4);

            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "jdk/internal/access/SharedSecrets",
                    "getJavaLangAccess",
                    "()Ljdk/internal/access/JavaLangAccess;",
                    false
            );
            methodVisitor.visitVarInsn(
                    Opcodes.ASTORE,
                    4
            );

            final Label l5 = new Label();
            methodVisitor.visitLabel(l5);

            methodVisitor.visitVarInsn(
                    Opcodes.ALOAD,
                    4
            );
            methodVisitor.visitVarInsn(
                    Opcodes.ALOAD,
                    2
            );
            methodVisitor.visitLdcInsn(
                    "jdk.internal.access"
            );
            methodVisitor.visitVarInsn(
                    Opcodes.ALOAD,
                    3
            );
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    "jdk/internal/access/JavaLangAccess",
                    "addExports",
                    "(Ljava/lang/Module;Ljava/lang/String;Ljava/lang/Module;)V",
                    true
            );

            final Label l6 = new Label();
            methodVisitor.visitLabel(l6);

            methodVisitor.visitInsn(
                    Opcodes.RETURN
            );

            methodVisitor.visitLocalVariable(
                    "injectorClass",
                    "Ljava/lang/Class;",
                    "Ljava/lang/Class<*>;",
                    l1,
                    l6,
                    0
            );
            methodVisitor.visitLocalVariable(
                    "loaderClass",
                    "Ljava/lang/Class;",
                    "Ljava/lang/Class<*>;",
                    l2,
                    l6,
                    1
            );
            methodVisitor.visitLocalVariable(
                    "javaBaseModule",
                    "Ljava/lang/Module;",
                    null,
                    l3,
                    l6,
                    2
            );
            methodVisitor.visitLocalVariable(
                    "loaderModule",
                    "Ljava/lang/Module;",
                    null,
                    l4,
                    l6,
                    3
            );
            methodVisitor.visitLocalVariable(
                    "javaLangAccess",
                    "Ljdk/internal/access/JavaLangAccess;",
                    null,
                    l5,
                    l6,
                    4
            );

            methodVisitor.visitMaxs(4, 5);
            methodVisitor.visitEnd();
        }

        classWriter.visitEnd();

        return classWriter.toByteArray();
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Injector() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(Injector.class) + " cannot be instantiated");
    }

    /**
     * A custom (and very bare-bones) implementation of {@link ClassLoader} to be used in conjunction with the class generated by {@link Injector#generateIn(String)}.
     *
     * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
     * @version 1.0
     * @since 1.0
     */
    public static final class Loader extends ClassLoader {
        /**
         * Public no-args constructor. <br>
         * Only meant to be used by {@link JavaLangAccessBridge}.
         */
        public Loader() {
            super(Loader.class.getClassLoader());
        }

        /**
         * Converts an array of bytes into an instance of {@link Class}.
         *
         * @param classBytes The class bytes
         * @return the {@code Class} object created from the data
         */
        @NotNull
        private Class<?> define(byte @NotNull [] classBytes) {
            return this.defineClass(null, classBytes, 0, classBytes.length, null);
        }

        /**
         * Converts an array of bytes into an instance of {@link Class}, and then initializes said class.
         *
         * @param classBytes The class bytes
         * @return the {@code Class} object created from the data
         */
        @SuppressWarnings("UnusedReturnValue")
        public Class<?> defineAndLoad(byte @NotNull [] classBytes) {
            return ThrowingExecutable.execute(
                    () -> Class.forName(this.define(classBytes).getName(), true, this)
            );
        }
    }
}
