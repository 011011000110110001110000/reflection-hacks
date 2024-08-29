package reflection.hacks.api.reflect;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reflection.hacks.api.invoke.Handles;
import reflection.hacks.api.invoke.Lookups;
import reflection.hacks.internal.util.Lazy;
import reflection.hacks.internal.util.function.ThrowingExecutable;

import java.lang.invoke.MethodHandle;
import java.security.ProtectionDomain;
import java.util.Optional;

/**
 * This class provides an API to deal with enumeration, definition and loading of classes, as well as other miscellaneous operations on {@link Class} objects.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @since 1.0
 */
public final class Classes {

    private static final Lazy<MethodHandle> DEFINE_CLASS_MH;

    static {
        DEFINE_CLASS_MH = Lazy.of(() -> Handles.findVirtual(ClassLoader.class, "defineClass", Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class));
    }

    /**
     * Ensures the specified classes are initialized.
     *
     * @param classes The classes to be initialized
     * @return an array containing the initialized classes
     * @see Classes#ensureInitialized(Class)
     */
    @NotNull
    @Contract("_ -> param1")
    public static Class<?>[] ensureInitialized(final @NotNull Class<?> @NotNull ... classes) {
        for (final Class<?> clazz : classes) {
            Classes.ensureInitialized(clazz);
        }

        return classes;
    }

    /**
     * Ensures that {@code clazz} has been initialized.
     *
     * @param clazz The class to be initialized
     * @param <T>   The type of the class modeled by {@code clazz}
     * @return the initialized class
     */
    @NotNull
    @Contract("_ -> param1")
    public static <T> Class<T> ensureInitialized(final @NotNull Class<T> clazz) {
        ThrowingExecutable.execute(
                () ->
                        Lookups.lookupIn(clazz) // We need a lookup in the specified class because of how the access checking is done
                                .ensureInitialized(clazz)
        );

        // Return clazz itself to avoid an unchecked cast to Class<T> (or a call to Classes#unchecked(Object)) that would be needed
        // due to how the generic type parameters on Lookup#ensureInitialized(Class) are resolved
        return clazz;
    }

    /**
     * Gets the <em>full</em> name of the specified class, including the name of the module it belongs to.
     *
     * @param clazz The class whose name is to be determined
     * @return a String obtained by concatenating the name of the module the specified class is defined in and the name of the class itself, separated by the <code>'/'</code> character
     * @apiNote If {@code clazz} is in an unnamed module, then the name of the module is the output of {@link Module#toString()}, which will be in the form {@code "unnamed module@####"},
     * where {@code ####} represents the module object's {@linkplain System#identityHashCode(Object) identity hash-code} in hexadecimal format.
     */
    @NotNull
    public static String moduleInclusiveName(final @NotNull Class<?> clazz) {
        final Module module = clazz.getModule();

        return (module.isNamed() ? module.getName() : module.toString()) + '/' + clazz.getName();
    }

    /**
     * Performs an unchecked cast to {@code T} of the given object {@code o}.
     *
     * @param o   The object to be cast to {@code T}
     * @param <T> The type that {@code o} should be cast to
     * @return The value of {@code o} cast to the appropriate type {@code T}
     */
    @Contract("null -> null; !null -> !null")
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends @Nullable Object> T unchecked(final @Nullable Object o) {
        return (T) o;
    }

    /**
     * Constructs a new class from an array of bytes containing a
     * class definition in class file format with the given loader.
     *
     * @param classBytes a memory image of a class file
     * @return the newly defined {@link Class}
     * @see Classes#define(ClassLoader, String, byte[], int, int)
     */
    public static Class<?> define(final @NotNull ClassLoader loader, final byte @NotNull [] classBytes) {
        return Classes.define(loader, null, classBytes, 0, classBytes.length);
    }

    /**
     * Constructs a new class from an array of bytes containing a
     * class definition in class file format with the given loader.
     *
     * @param loader     the {@link ClassLoader} that will be used to define the class
     * @param className  the name of the new class
     * @param classBytes a memory image of a class file
     * @param offset     the offset into the classBytes
     * @param length     the length of the class file
     * @return the newly defined {@link Class}
     * @see ClassLoader#defineClass(String, byte[], int, int)
     */
    @NotNull
    public static Class<?> define(final @NotNull ClassLoader loader, final @Nullable String className, final byte @NotNull [] classBytes, final int offset, final int length) {
        return Classes.define(loader, className, classBytes, offset, length, null);
    }

    /**
     * Constructs a new class from an array of bytes containing a
     * class definition in class file format with the given loader
     * and assigns the new class to the specified protection domain.
     *
     * @param loader           the {@link ClassLoader} that will be used to define the class
     * @param className        the name of the new class
     * @param classBytes       a memory image of a class file
     * @param offset           the offset into the classBytes
     * @param length           the length of the class file
     * @param protectionDomain the protection domain that the class should belong to
     * @return the newly defined {@link Class}
     * @see ClassLoader#defineClass(String, byte[], int, int, ProtectionDomain)
     */
    @NotNull
    public static Class<?> define(final @NotNull ClassLoader loader, final @Nullable String className, final byte @NotNull [] classBytes, final int offset, final int length, final @Nullable ProtectionDomain protectionDomain) {
        return ThrowingExecutable.execute(
                () -> (Class<?>) Classes.DEFINE_CLASS_MH.get().invokeExact(loader, className, classBytes, offset, length, protectionDomain)
        );
    }

    /**
     * Attempts to load the class with the given name using the caller's class loader.
     *
     * @param name The binary name of the class
     * @return the loaded class
     * @apiNote As specified in the documentation for {@link Class#getClassLoader()}, {@code null} may be
     * used to represent the bootstrap class loader. However, {@link Classes#load(String, ClassLoader)} expects
     * the supplied {@link ClassLoader} to be not-{@code null}. Because of this, the result of invoking this
     * method from a class loaded by the bootstrap class loader will be the throwing of a {@link NullPointerException}
     * in implementations that choose to represent the bootstrap class loader with {@code null}.
     * In practice, most if not all JDK implementations will choose this approach, as the bootstrap class loader
     * should not be accessible by code outside the JDK implementation.
     * It is therefore necessary to ensure that this method can never end up being called directly by a class loaded by the bootstrap class loader.
     * @see Classes#load(String, ClassLoader)
     */
    @NotNull
    public static <T> Class<T> load(final @NotNull String name) {
        return Classes.load(name, Reflection.STACK_WALKER.getCallerClass().getClassLoader());
    }

    /**
     * Attempts to load the class with the given name using the caller's class loader.
     *
     * @param name The binary name of the class
     * @param <T>  The type of the class modeled by the {@link Class} with the given {@code name}
     * @return an {@link Optional} instance containing the loaded class if found
     * @apiNote As specified in the documentation for {@link Class#getClassLoader()}, {@code null} may be
     * used to represent the bootstrap class loader. However, {@link Classes#tryLoad(String, ClassLoader)} expects
     * the supplied {@link ClassLoader} to be not-{@code null}. Because of this, the result of invoking this
     * method from a class loaded by the bootstrap class loader will be the throwing of a {@link NullPointerException}
     * in implementations that choose to represent the bootstrap class loader with {@code null}.
     * In practice, most if not all JDK implementations will choose this approach, as the bootstrap class loader
     * should not be accessible by code outside the JDK implementation.
     * It is therefore necessary to ensure that this method can never end up being called directly by a class loaded by the bootstrap class loader.
     * @see Classes#load(String)
     * @see Classes#tryLoad(String, ClassLoader)
     */
    @NotNull
    public static <T> Optional<Class<T>> tryLoad(final @NotNull String name) {
        return Classes.tryLoad(name, Reflection.STACK_WALKER.getCallerClass().getClassLoader());
    }

    /**
     * Attempts to load the class with the given name using the system class loader.
     *
     * @param name The binary name of the class
     * @param <T>  The type of the class modeled by the {@link Class} with the given {@code name}
     * @return the loaded class
     * @see Classes#load(String, ClassLoader)
     * @see ClassLoader#getSystemClassLoader()
     */
    @NotNull
    public static <T> Class<T> loadWithSystemLoader(final @NotNull String name) {
        return Classes.load(name, ClassLoader.getSystemClassLoader());
    }

    /**
     * Attempts to load the class with the given name using the system class loader.
     *
     * @param name The binary name of the class
     * @param <T>  The type of the class modeled by the {@link Class} with the given {@code name}
     * @return an {@link Optional} instance containing the loaded class if found
     * @see Classes#load(String, ClassLoader)
     * @see ClassLoader#getSystemClassLoader()
     */
    @NotNull
    public static <T> Optional<Class<T>> tryLoadWithSystemLoader(final @NotNull String name) {
        return Classes.tryLoad(name, ClassLoader.getSystemClassLoader());
    }

    /**
     * Attempts to load the class with the given name using the platform class loader.
     *
     * @param name The binary name of the class
     * @param <T>  The type of the class modeled by the {@link Class} with the given {@code name}
     * @return the loaded class
     * @see Classes#load(String, ClassLoader)
     * @see ClassLoader#getPlatformClassLoader()
     */
    @NotNull
    public static <T> Class<T> loadWithPlatformLoader(final @NotNull String name) {
        return Classes.load(name, ClassLoader.getPlatformClassLoader());
    }

    /**
     * Attempts to load the class with the given name using the platform class loader.
     *
     * @param name The binary name of the class
     * @param <T>  The type of the class modeled by the {@link Class} with the given {@code name}
     * @return an {@link Optional} instance containing the loaded class if found
     * @see Classes#load(String, ClassLoader)
     * @see ClassLoader#getPlatformClassLoader()
     */
    @NotNull
    public static <T> Optional<Class<T>> tryLoadWithPlatformLoader(final @NotNull String name) {
        return Classes.tryLoad(name, ClassLoader.getPlatformClassLoader());
    }

    /**
     * Attempts to load the class with the given binary name using the given {@link ClassLoader}.
     *
     * @param name   The binary name of the class
     * @param loader The class loader to use for loading the class
     * @param <T>    The type of the class modeled by the {@link Class} with the given {@code name}
     * @return the loaded class
     * @see ClassLoader#loadClass(String)
     */
    @NotNull
    public static <T> Class<T> load(final @NotNull String name, final @NotNull ClassLoader loader) {
        // It will always return a non-null value or throw a ClassNotFoundException

        return Classes.unchecked(
                ThrowingExecutable.execute(
                        () -> loader.loadClass(name)
                )
        );
    }

    /**
     * Attempts to load the class with the given binary name using the given {@link ClassLoader}.
     *
     * @param name   The binary name of the class
     * @param loader The class loader to use for loading the class
     * @param <T>    The type of the class modeled by the {@link Class} with the given {@code name}
     * @return an {@link Optional} instance containing the loaded class if found
     * @see ClassLoader#loadClass(String)
     */
    @NotNull
    public static <T> Optional<Class<T>> tryLoad(final @NotNull String name, final @NotNull ClassLoader loader) {
        try {
            return Optional.of(Classes.unchecked(loader.loadClass(name)));
        } catch (final ClassNotFoundException cnfe) {
            return Optional.empty();
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Classes() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(Classes.class) + " cannot be instantiated");
    }

}
