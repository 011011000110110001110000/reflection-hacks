package reflection.hacks.api.reflect;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reflection.hacks.api.invoke.Handles;
import reflection.hacks.internal.access.JavaLangAccessBridge;
import reflection.hacks.internal.util.Throwables;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * This class provides an API for performing operations on {@link Module}s such as {@linkplain Modules#addExports(Module, String, Module) exporting} and
 * {@linkplain Modules#addOpens(Module, String, Module) opening} packages, or {@linkplain Modules#addEnableNativeAccess(Module) enabling a module's access to native methods}.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 */
public final class Modules {

    /**
     * Special module that represents all unnamed modules (see {@link Module#ALL_UNNAMED_MODULE})
     */
    @NotNull
    private static final Module ALL_UNNAMED_MODULE;

    /**
     * Special module that represents all modules (see {@link Module#EVERYONE_MODULE})
     */
    @NotNull
    private static final Module EVERYONE_MODULE;

    /**
     * Cached {@link MethodHandle} for {@link ModuleLayer.Controller#Controller(ModuleLayer)}
     *
     * @see #getControllerForLayer(ModuleLayer)
     */
    @NotNull
    private static final MethodHandle LAYER_CONTROLLER_CONSTRUCTOR_MH;

    static {

        // Open the java.lang package to this class' module, so that we can freely invoke
        // AccessibleObject#setAccessible(boolean) on members of the package.
        JavaLangAccessBridge.addOpens(Object.class.getModule(), Object.class.getPackageName(), Modules.class.getModule());

        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        try {
            final Constructor<ModuleLayer.Controller> controllerConstructor = ModuleLayer.Controller.class.getDeclaredConstructor(ModuleLayer.class);
            final Field allUnnamedModuleField = Module.class.getDeclaredField("ALL_UNNAMED_MODULE");
            final Field everyoneModuleField = Module.class.getDeclaredField("EVERYONE_MODULE");

            controllerConstructor.setAccessible(true);
            allUnnamedModuleField.setAccessible(true);
            everyoneModuleField.setAccessible(true);

            LAYER_CONTROLLER_CONSTRUCTOR_MH = lookup.unreflectConstructor(controllerConstructor);
            ALL_UNNAMED_MODULE = Classes.unchecked(allUnnamedModuleField.get(null));
            EVERYONE_MODULE = Classes.unchecked(everyoneModuleField.get(null));
        } catch (Throwable t) {
            throw Throwables.sneakyThrow(t);
        }

    }

    /**
     * Exports the package with the given name from the {@code source} module to the {@code target} module.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     * @param target      The module the package is to be exported to
     */
    public static void addExports(final @NotNull Module source, final @NotNull String packageName, final @NotNull Module target) {
        JavaLangAccessBridge.addExports(source, packageName, target);
    }

    /**
     * Exports the package with the given name from the {@code source} module to all unnamed modules.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     */
    public static void addExportsToAllUnnamed(final @NotNull Module source, final @NotNull String packageName) {
        JavaLangAccessBridge.addExportsToAllUnnamed(source, packageName);
    }

    /**
     * Exports the package with the given name from the {@code source} module to all modules.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     */
    public static void addExports(final @NotNull Module source, final @NotNull String packageName) {
        JavaLangAccessBridge.addExports(source, packageName);
    }

    /**
     * Opens the package with the given name from the {@code source} module to the {@code target} module.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     * @param target      The module the package is to be opened to
     */
    public static void addOpens(final @NotNull Module source, final @NotNull String packageName, final @NotNull Module target) {
        JavaLangAccessBridge.addOpens(source, packageName, target);
    }

    /**
     * Opens the package with the given name from the {@code source} module to all unnamed modules.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     */
    public static void addOpensToAllUnnamed(final @NotNull Module source, final @NotNull String packageName) {
        JavaLangAccessBridge.addOpensToAllUnnamed(source, packageName);
    }

    /**
     * Opens the package with the given name from the {@code source} module to all modules.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     * @implNote Since {@link jdk.internal.access.JavaLangAccess} does not expose any methods to unconditionally open a package to all modules,
     * we use the special {@link #EVERYONE_MODULE} instance treating it just like another regular module.
     */
    public static void addOpens(final @NotNull Module source, final @NotNull String packageName) {
        Modules.addOpens(source, packageName, EVERYONE_MODULE);
    }

    /**
     * Updates module {@code m} to allow access to restricted methods.
     *
     * @param m The module to update
     * @apiNote This method is only useful when using the foreign function and memory APIs.
     */
    public static void addEnableNativeAccess(final @NotNull Module m) {
        JavaLangAccessBridge.addEnableNativeAccess(m);
    }

    /**
     * Updates all unnamed modules to allow access to restricted methods.
     *
     * @apiNote This method is only useful when using the foreign function and memory APIs.
     */
    public static void addEnableNativeAccessToAllUnnamed() {
        JavaLangAccessBridge.addEnableNativeAccess(ALL_UNNAMED_MODULE);
    }

    /**
     * Exports the package with the given name from the {@code source} module to the {@code target} module using an instance of {@link ModuleLayer.Controller}.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     * @param target      The module the package is to be exported to
     * @return the newly created {@link ModuleLayer.Controller} instance that was used to export the package
     * @throws UnsupportedOperationException if {@code source} is not in a {@link ModuleLayer}
     */
    @Contract("_, _, _ -> new")
    @NotNull
    public static ModuleLayer.Controller addExportsWithController(final @NotNull Module source, final @NotNull String packageName, final @NotNull Module target) {
        return Modules.getControllerForModule(source).addExports(source, packageName, target);
    }

    /**
     * Exports the package with the given name from the {@code source} module to all unnamed modules using an instance of {@link ModuleLayer.Controller}.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     * @return the newly created {@link ModuleLayer.Controller} instance that was used to export the package
     * @throws UnsupportedOperationException if {@code source} is not in a {@link ModuleLayer}
     */
    @Contract("_, _ -> new")
    @NotNull
    public static ModuleLayer.Controller addExportsToAllUnnamedWithController(final @NotNull Module source, final @NotNull String packageName) {
        return Modules.addExportsWithController(source, packageName, ALL_UNNAMED_MODULE);
    }

    /**
     * Exports the package with the given name from the {@code source} module to all modules using an instance of {@link ModuleLayer.Controller}.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     * @return the newly created {@link ModuleLayer.Controller} instance that was used to export the package
     * @throws UnsupportedOperationException if {@code source} is not in a {@link ModuleLayer}
     */
    @Contract("_, _ -> new")
    @NotNull
    public static ModuleLayer.Controller addExportsWithController(final @NotNull Module source, final @NotNull String packageName) {
        return Modules.addExportsWithController(source, packageName, EVERYONE_MODULE);
    }

    /**
     * Opens the package with the given name from the {@code source} module to the {@code target} module using an instance of {@link ModuleLayer.Controller}.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     * @param target      The module the package is to be opened to
     * @return the newly created {@link ModuleLayer.Controller} instance that was used to open the package
     * @throws UnsupportedOperationException if {@code source} is not in a {@link ModuleLayer}
     */
    @Contract("_, _, _ -> new")
    @NotNull
    public static ModuleLayer.Controller addOpensWithController(final @NotNull Module source, final @NotNull String packageName, final @NotNull Module target) {
        return Modules.getControllerForModule(source).addOpens(source, packageName, target);
    }

    /**
     * Opens the package with the given name from the {@code source} module to all unnamed modules using an instance of {@link ModuleLayer.Controller}.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     * @return the newly created {@link ModuleLayer.Controller} instance that was used to open the package
     * @throws UnsupportedOperationException if {@code source} is not in a {@link ModuleLayer}
     */
    @Contract("_, _ -> new")
    @NotNull
    public static ModuleLayer.Controller addOpensToAllUnnamedWithController(final @NotNull Module source, final @NotNull String packageName) {
        return Modules.addOpensWithController(source, packageName, ALL_UNNAMED_MODULE);
    }

    /**
     * Opens the package with the given name from the {@code source} module to all modules using an instance of {@link ModuleLayer.Controller}.
     *
     * @param source      The module the package belongs to
     * @param packageName The name of the package
     * @return the newly created {@link ModuleLayer.Controller} instance that was used to open the package
     * @throws UnsupportedOperationException if {@code source} is not in a {@link ModuleLayer}
     */
    @Contract("_, _ -> new")
    @NotNull
    public static ModuleLayer.Controller addOpensWithController(final @NotNull Module source, final @NotNull String packageName) {
        return Modules.addOpensWithController(source, packageName, EVERYONE_MODULE);
    }

    /**
     * Produces a {@link ModuleLayer.Controller} instance that controls the layer the given module belongs to.
     *
     * @param module The module whose layer is to be controlled
     * @return the {@link ModuleLayer.Controller} instance
     * @throws UnsupportedOperationException if {@code module} is not in a {@link ModuleLayer}
     */
    @Contract("_ -> new")
    @NotNull
    public static ModuleLayer.Controller getControllerForModule(final @NotNull Module module) {
        final ModuleLayer layer = module.getLayer();

        if (layer == null) {
            throw new UnsupportedOperationException("Cannot obtain a controller instance for module " + module.getName() + " because it is not in any layer");
        }

        return Modules.getControllerForLayer(layer);
    }

    /**
     * Produces a {@link ModuleLayer.Controller} instance that controls the given {@link ModuleLayer}.
     *
     * @param layer The layer to be controlled
     * @return the {@link ModuleLayer.Controller} instance
     */
    @Contract("_ -> new")
    @NotNull
    public static ModuleLayer.Controller getControllerForLayer(final @NotNull ModuleLayer layer) {
        return Handles.invoke(LAYER_CONTROLLER_CONSTRUCTOR_MH, layer);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Modules() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(Modules.class) + " cannot be instantiated");
    }

}
