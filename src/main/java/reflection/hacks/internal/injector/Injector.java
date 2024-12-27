package reflection.hacks.internal.injector;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import reflection.hacks.api.reflect.Classes;
import reflection.hacks.internal.access.JavaLangAccessBridge;
import reflection.hacks.internal.util.function.ThrowingExecutable;

/**
 * Helper class that generates an {@code Injected} class inside the given package.
 * <p>
 * The source of the generated class is as follows:
 * <blockquote><pre>{@code
 * public class Injected {
 *     static {
 *         final Class<?> injectedClass = Injected.class;
 *         final Class<?> loaderClass = injectedClass.getClassLoader().getClass();
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
 * This is used to define and load a class inside a proxy module which has access to the {@link jdk.internal.access} package.
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

        classWriter.visit(
                Opcodes.V17,
                Opcodes.ACC_SUPER,
                fullInjectedClassName,
                null,
                "java/lang/Object",
                null
        );

        // Static class initializer bytecode (<clinit>()V)
        {
            final MethodVisitor clinitVisitor = classWriter.visitMethod(
                    Opcodes.ACC_STATIC,
                    "<clinit>",
                    "()V",
                    null,
                    null
            );

            clinitVisitor.visitCode();

            clinitVisitor.visitLdcInsn(
                    Type.getType(injectorClassDescriptor)
            );
            clinitVisitor.visitVarInsn(
                    Opcodes.ASTORE,
                    0
            );
            clinitVisitor.visitVarInsn(
                    Opcodes.ALOAD,
                    0
            );
            clinitVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Class",
                    "getClassLoader",
                    "()Ljava/lang/ClassLoader;",
                    false
            );
            clinitVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Object",
                    "getClass",
                    "()Ljava/lang/Class;",
                    false
            );
            clinitVisitor.visitVarInsn(
                    Opcodes.ASTORE, 1
            );
            clinitVisitor.visitLdcInsn(
                    Type.getType("Ljava/lang/Object;")
            );
            clinitVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Class",
                    "getModule",
                    "()Ljava/lang/Module;",
                    false
            );
            clinitVisitor.visitVarInsn(
                    Opcodes.ASTORE,
                    2
            );
            clinitVisitor.visitVarInsn(
                    Opcodes.ALOAD,
                    1
            );
            clinitVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Class",
                    "getModule",
                    "()Ljava/lang/Module;",
                    false
            );
            clinitVisitor.visitVarInsn(
                    Opcodes.ASTORE,
                    3
            );

            clinitVisitor.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "jdk/internal/access/SharedSecrets",
                    "getJavaLangAccess",
                    "()Ljdk/internal/access/JavaLangAccess;",
                    false
            );
            clinitVisitor.visitVarInsn(
                    Opcodes.ASTORE,
                    4
            );
            clinitVisitor.visitVarInsn(
                    Opcodes.ALOAD,
                    4
            );
            clinitVisitor.visitVarInsn(
                    Opcodes.ALOAD,
                    2
            );
            clinitVisitor.visitLdcInsn(
                    "jdk.internal.access"
            );
            clinitVisitor.visitVarInsn(
                    Opcodes.ALOAD,
                    3
            );
            clinitVisitor.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    "jdk/internal/access/JavaLangAccess",
                    "addExports",
                    "(Ljava/lang/Module;Ljava/lang/String;Ljava/lang/Module;)V",
                    true
            );
            clinitVisitor.visitInsn(
                    Opcodes.RETURN
            );

            clinitVisitor.visitMaxs(4, 5);
            clinitVisitor.visitEnd();
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
