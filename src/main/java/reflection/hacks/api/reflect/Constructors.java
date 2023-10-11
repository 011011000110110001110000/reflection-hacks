package reflection.hacks.api.reflect;

import org.jetbrains.annotations.NotNull;
import reflection.hacks.internal.util.function.ThrowingExecutable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;

/**
 * This class provides an API to retrieve the {@link Constructor}s declared by a class.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @since 1.0
 */
public final class Constructors {

    /**
     * Retrieves the {@linkplain Constructor#getRoot() root Constructor} with the given parameter types declared by class {@code owner}
     * and makes it accessible.
     *
     * @param owner          The class from which the constructor is accessed
     * @param parameterTypes The parameter types of the constructor, in order
     * @param <T>            The type represented by {@code owner}
     * @return The constructor with the given parameter types declared by class {@code owner}
     * @see AccessibleObjects#getRoot(AccessibleObject)
     * @see AccessibleObjects#setAccessible(AccessibleObject)
     */
    @NotNull
    public static <T extends @NotNull Object> Constructor<T> findAccessibleRoot(final @NotNull Class<T> owner, final @NotNull Class<?>... parameterTypes) {
        final Constructor<T> constructor = Constructors.findRoot(owner, parameterTypes);
        AccessibleObjects.setAccessible(constructor);
        return constructor;
    }

    /**
     * Retrieves the {@linkplain Constructor#getRoot() root Constructor} with the given parameter types declared by class {@code owner}.
     *
     * @param owner          The class from which the constructor is accessed
     * @param parameterTypes The parameter types of the constructor, in order
     * @param <T>            The type represented by {@code owner}
     * @return The constructor with the given parameter types declared by class {@code owner}
     * @see AccessibleObjects#getRoot(AccessibleObject)
     */
    @NotNull
    public static <T extends @NotNull Object> Constructor<T> findRoot(final @NotNull Class<T> owner, final @NotNull Class<?>... parameterTypes) {
        // noinspection DataFlowIssue
        return AccessibleObjects.getRoot(Constructors.find(owner, parameterTypes));
    }

    /**
     * Retrieves the {@link Constructor} with the given parameter types declared by class {@code owner}
     * and makes it accessible.
     *
     * @param owner          The class from which the constructor is accessed
     * @param parameterTypes The parameter types of the constructor, in order
     * @param <T>            The type represented by {@code owner}
     * @return The constructor with the given parameter types declared by class {@code owner}
     * @see AccessibleObjects#setAccessible(AccessibleObject)
     */
    @NotNull
    public static <T extends @NotNull Object> Constructor<T> findAccessible(final @NotNull Class<T> owner, final @NotNull Class<?>... parameterTypes) {
        final Constructor<T> constructor = Constructors.find(owner, parameterTypes);
        AccessibleObjects.setAccessible(constructor);
        return constructor;
    }

    /**
     * Retrieves the {@link Constructor} with the given parameter types declared by class {@code owner}.
     *
     * @param owner          The class from which the constructor is accessed
     * @param parameterTypes The parameter types of the constructor, in order
     * @param <T>            The type represented by {@code owner}
     * @return The constructor with the given parameter types declared by class {@code owner}
     */
    @NotNull
    public static <T extends @NotNull Object> Constructor<T> find(final @NotNull Class<T> owner, final @NotNull Class<?>... parameterTypes) {
        return ThrowingExecutable.execute(
                () -> owner.getDeclaredConstructor(parameterTypes)
        );
    }

    /**
     * Retrieves the {@linkplain Constructor#getRoot() roots} of the {@link Constructor}s declared by class {@code owner}
     * and makes them accessible.
     *
     * @param owner The class from which the constructors are accessed
     * @param <T>   The type represented by {@code owner}
     * @return an array containing the {@link Constructor}s declared by class {@code owner}
     * @see Constructors#getRoot(Class)
     * @see AccessibleObjects#setAccessible(AccessibleObject...)
     */
    @NotNull
    public static <T extends @NotNull Object> Constructor<T> @NotNull [] getAccessibleRoots(final @NotNull Class<T> owner) {
        final Constructor<T>[] constructors = Constructors.getRoot(owner);
        AccessibleObjects.setAccessible(constructors);
        return constructors;
    }

    /**
     * Retrieves the {@linkplain Constructor#getRoot() roots} of the {@link Constructor}s declared by class {@code owner}.
     *
     * @param owner The class from which the constructors are accessed
     * @param <T>   The type represented by {@code owner}
     * @return an array containing the {@link Constructor}s declared by class {@code owner}
     * @see Constructors#getRoot(Class)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends @NotNull Object> Constructor<T> @NotNull [] getRoot(final @NotNull Class<T> owner) {
        final Constructor<T>[] constructors = (Constructor<T>[]) owner.getDeclaredConstructors();

        for (int i = 0; i < constructors.length; i++) {
            final Constructor<T> constructor = AccessibleObjects.getRoot(constructors[i]);
            constructors[i] = constructor;
        }

        return constructors;
    }

    /**
     * Retrieves the {@link Constructor}s declared by class {@code owner} and makes them accessible.
     *
     * @param owner The class from which the constructors are accessed
     * @param <T>   The type represented by {@code owner}
     * @return an array containing the {@link Constructor}s declared by class {@code owner}
     * @see AccessibleObjects#setAccessible(AccessibleObject...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends @NotNull Object> Constructor<T> @NotNull [] getAccessible(final @NotNull Class<T> owner) {
        final Constructor<T>[] constructors = (Constructor<T>[]) owner.getDeclaredConstructors();
        AccessibleObjects.setAccessible(constructors);
        return constructors;
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Constructors() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(Constructors.class) + " cannot be instantiated");
    }

}
