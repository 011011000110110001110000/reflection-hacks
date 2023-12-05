package reflection.hacks.internal.access;

import org.jetbrains.annotations.NotNull;
import reflection.hacks.api.invoke.Handles;
import reflection.hacks.api.reflect.Classes;
import reflection.hacks.api.reflect.Reflection;
import reflection.hacks.internal.injector.Injector;
import reflection.hacks.internal.util.Throwables;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Proxy;

/**
 * Helper class that serves as a bridge between {@link Reflection} (and its internals) and {@link jdk.internal.access.JavaLangAccess}. <br>
 * The purpose of this class is to get rid of the need to export the "jdk.internal.access" package to this class' module via the {@code --add-exports}
 * argument at compile / run time.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @since 1.0
 */
public final class JavaLangAccessBridge {

    /**
     * Cached {@link MethodHandle} for {@link jdk.internal.access.JavaLangAccess#addExports(Module, String, Module)}
     *
     * @see JavaLangAccessBridge#addExports(Module, String, Module)
     */
    @NotNull
    private static final MethodHandle ADD_EXPORTS_TO_MODULE_MH;

    /**
     * Cached {@link MethodHandle} for {@link jdk.internal.access.JavaLangAccess#addExportsToAllUnnamed(Module, String)}
     *
     * @see JavaLangAccessBridge#addExportsToAllUnnamed(Module, String)
     */
    @NotNull
    private static final MethodHandle ADD_EXPORTS_TO_ALL_UNNAMED_MODULES_MH;

    /**
     * Cached {@link MethodHandle} for {@link jdk.internal.access.JavaLangAccess#addExports(Module, String)}
     *
     * @see JavaLangAccessBridge#addExports(Module, String)
     */
    @NotNull
    private static final MethodHandle ADD_EXPORTS_TO_ALL_MODULES_MH;

    /**
     * Cached {@link MethodHandle} for {@link jdk.internal.access.JavaLangAccess#addOpens(Module, String, Module)}
     *
     * @see JavaLangAccessBridge#addOpens(Module, String, Module)
     */
    @NotNull
    private static final MethodHandle ADD_OPENS_TO_MODULE_MH;

    /**
     * Cached {@link MethodHandle} for {@link jdk.internal.access.JavaLangAccess#addOpensToAllUnnamed(Module, String)}
     *
     * @see JavaLangAccessBridge#addOpensToAllUnnamed(Module, String)
     */
    @NotNull
    private static final MethodHandle ADD_OPENS_TO_ALL_UNNAMED_MODULES_MH;

    /**
     * Cached {@link MethodHandle} for {@link jdk.internal.access.JavaLangAccess#addEnableNativeAccess(Module)}
     *
     * @see JavaLangAccessBridge#addEnableNativeAccess(Module)
     */
    @NotNull
    private static final MethodHandle ADD_ENABLE_NATIVE_ACCESS_TO_MODULE_MH;

    static {

        JavaLangAccessBridge.gainInternalAccess();
        try {
            // noinspection Java9ReflectionClassVisibility
            final Class<?> sharedSecretsClass = Class.forName("jdk.internal.access.SharedSecrets");
            // noinspection Java9ReflectionClassVisibility
            final Class<?> javaLangAccessClass = Class.forName("jdk.internal.access.JavaLangAccess");

            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            final MethodHandle getJavaLangAccessHandle = lookup.findStatic(sharedSecretsClass, "getJavaLangAccess", MethodType.methodType(javaLangAccessClass));

            final Object javaLangAccessInstance = getJavaLangAccessHandle.invoke();

            ADD_EXPORTS_TO_MODULE_MH = lookup.findVirtual(
                    javaLangAccessClass,
                    "addExports",
                    MethodType.methodType(
                            void.class,
                            Module.class,
                            String.class,
                            Module.class
                    )
            ).bindTo(javaLangAccessInstance);

            ADD_EXPORTS_TO_ALL_UNNAMED_MODULES_MH = lookup.findVirtual(
                    javaLangAccessClass,
                    "addExportsToAllUnnamed",
                    MethodType.methodType(
                            void.class,
                            Module.class,
                            String.class
                    )
            ).bindTo(javaLangAccessInstance);

            ADD_EXPORTS_TO_ALL_MODULES_MH = lookup.findVirtual(
                    javaLangAccessClass,
                    "addExports",
                    MethodType.methodType(
                            void.class,
                            Module.class,
                            String.class
                    )
            ).bindTo(javaLangAccessInstance);

            ADD_OPENS_TO_MODULE_MH = lookup.findVirtual(
                    javaLangAccessClass,
                    "addOpens",
                    MethodType.methodType(
                            void.class,
                            Module.class,
                            String.class,
                            Module.class
                    )
            ).bindTo(javaLangAccessInstance);

            ADD_OPENS_TO_ALL_UNNAMED_MODULES_MH = lookup.findVirtual(
                    javaLangAccessClass,
                    "addOpensToAllUnnamed",
                    MethodType.methodType(
                            void.class,
                            Module.class,
                            String.class
                    )
            ).bindTo(javaLangAccessInstance);

            ADD_ENABLE_NATIVE_ACCESS_TO_MODULE_MH = lookup.findVirtual(
                    javaLangAccessClass,
                    "addEnableNativeAccess",
                    MethodType.methodType(
                            Module.class,
                            Module.class
                    )
            ).bindTo(javaLangAccessInstance);
        } catch (Throwable t) {
            throw Throwables.sneakyThrow(t);
        }

    }

    /**
     * Bridge method for {@link jdk.internal.access.JavaLangAccess#addExports(Module, String, Module)}
     * <p>
     * Updates module {@code m1} to export a package to module {@code m2}.
     *
     * @param m1  The module that contains the package
     * @param pkg The name of the package to export
     * @param m2  The module the package should be exported to
     */
    public static void addExports(final @NotNull Module m1, final @NotNull String pkg, final @NotNull Module m2) {
        Handles.invoke(JavaLangAccessBridge.ADD_EXPORTS_TO_MODULE_MH, m1, pkg, m2);
    }

    /**
     * Bridge method for {@link jdk.internal.access.JavaLangAccess#addExportsToAllUnnamed(Module, String)}
     * <p>
     * Updates module {@code m} to export a package to all unnamed modules.
     *
     * @param m   The module that contains the package
     * @param pkg The name of the package to export
     */
    public static void addExportsToAllUnnamed(final @NotNull Module m, final @NotNull String pkg) {
        Handles.invoke(JavaLangAccessBridge.ADD_EXPORTS_TO_ALL_UNNAMED_MODULES_MH, m, pkg);
    }

    /**
     * Bridge method for {@link jdk.internal.access.JavaLangAccess#addExports(Module, String)}
     * <p>
     * Updates module {@code m} to export a package unconditionally.
     *
     * @param m   The module that contains the package
     * @param pkg The name of the package to export
     */
    public static void addExports(final @NotNull Module m, final @NotNull String pkg) {
        Handles.invoke(JavaLangAccessBridge.ADD_EXPORTS_TO_ALL_MODULES_MH, m, pkg);
    }

    /**
     * Bridge method for {@link jdk.internal.access.JavaLangAccess#addOpens(Module, String, Module)}
     * <p>
     * Updates module {@code m1} to open a package to module {@code m2}.
     *
     * @param m1  The module that contains the package
     * @param pkg The name of the package to open
     * @param m2  The module to open the package to
     */
    public static void addOpens(final @NotNull Module m1, final @NotNull String pkg, final @NotNull Module m2) {
        Handles.invoke(JavaLangAccessBridge.ADD_OPENS_TO_MODULE_MH, m1, pkg, m2);
    }

    /**
     * Bridge method for {@link jdk.internal.access.JavaLangAccess#addOpensToAllUnnamed(Module, String)}.
     * <p>
     * Updates module {@code m} to open a package to all unnamed modules.
     *
     * @param m   The module that contains the package
     * @param pkg The name of the package to open
     */
    public static void addOpensToAllUnnamed(final @NotNull Module m, final @NotNull String pkg) {
        Handles.invoke(JavaLangAccessBridge.ADD_OPENS_TO_ALL_UNNAMED_MODULES_MH, m, pkg);
    }

    /**
     * Bridge method for {@link jdk.internal.access.JavaLangAccess#addEnableNativeAccess(Module)}
     * <p>
     * Updates module {@code m} to allow access to restricted methods.
     *
     * @param m The module to update
     */
    public static void addEnableNativeAccess(final @NotNull Module m) {
        Handles.invoke(JavaLangAccessBridge.ADD_ENABLE_NATIVE_ACCESS_TO_MODULE_MH, m);
    }

    /**
     * Exploits the Proxy API to gain access to the {@link jdk.internal.access} package,
     * which is normally not visible to modules that are not part of the JDK implementation.
     *
     * @implNote TODO: add detailed explanation of how this works
     */
    private static void gainInternalAccess() {
        final String javaLangAccessName = "jdk.internal.access.JavaLangAccess";
        final Injector.Loader injectorLoader = new Injector.Loader();
        final Class<?> javaLangAccessInterface;
        final Class<?>[] interfaces;

        try {
            // noinspection Java9ReflectionClassVisibility
            javaLangAccessInterface = Class.forName(javaLangAccessName);
            interfaces = new Class[]{
                    javaLangAccessInterface
            };

            // noinspection SuspiciousInvocationHandlerImplementation
            final Object proxyInstance = Proxy.newProxyInstance(

                    injectorLoader,
                    interfaces,
                    (proxy, method, arguments) -> null
            );

            final String packageName = proxyInstance.getClass().getPackageName().replace(".", "/");

            injectorLoader.defineAndLoad(Injector.generateIn(packageName));

        } catch (ReflectiveOperationException roe) {
            throw new RuntimeException("Could not gain access to the jdk.internal.access package", roe);
        }

    }

    /**
     * Private constructor to prevent instantiation.
     */
    private JavaLangAccessBridge() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(JavaLangAccessBridge.class) + " cannot be instantiated");
    }

}
