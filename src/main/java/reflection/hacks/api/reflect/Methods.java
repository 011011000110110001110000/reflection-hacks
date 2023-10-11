package reflection.hacks.api.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reflection.hacks.api.invoke.Handles;
import reflection.hacks.internal.util.CompatHelper;
import reflection.hacks.internal.util.Throwables;
import reflection.hacks.internal.util.function.ThrowingExecutable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides an API to retrieve the {@link Method}s declared by a class.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @since 1.0
 */
public final class Methods {

    /**
     * Cached {@link MethodHandle} for the native method that retrieves the declared {@link Method}s of a {@link Class}
     */
    @NotNull
    private static final MethodHandle NATIVE_GET_DECLARED_METHODS_MH;

    static {

        NATIVE_GET_DECLARED_METHODS_MH = CompatHelper.nativeDeclaredMethodRetriever();

    }

    /**
     * Retrieves the {@link Method} with the given {@code name} and parameter types declared by class {@code owner}.
     * This method will disable any reflection filters set on methods declared by {@code owner}
     * before retrieving the method.
     *
     * @param owner          The class from which the method is accessed
     * @param name           The name of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the {@link Method} with the given name and parameter types declared by class {@code owner}
     */
    @NotNull
    public static Method unfilterAndFind(final @NotNull Class<?> owner, final @NotNull String name, final @Nullable Class<?>... parameterTypes) {
        Methods.unfilter(owner);
        return Methods.find(owner, name, parameterTypes);
    }

    /**
     * Retrieves the {@link Method} with the given {@code name} and parameter types declared by class {@code owner}
     * and makes it accessible. This method will disable any reflection filters set on methods declared by {@code owner}
     * before retrieving the method.
     *
     * @param owner          The class from which the method is accessed
     * @param name           The name of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the {@link Method} with the given name and parameter types declared by class {@code owner}
     */
    @NotNull
    public static Method unfilterAndFindAccessible(final @NotNull Class<?> owner, final @NotNull String name, final @Nullable Class<?>... parameterTypes) {
        Methods.unfilter(owner);
        return Methods.findAccessible(owner, name, parameterTypes);
    }

    /**
     * Retrieves the {@link Method} with the given {@code name} and parameter types declared by class {@code owner}
     * and makes it accessible.
     *
     * @param owner          The class from which the method is accessed
     * @param name           The name of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the {@link Method} with the given name and parameter types declared by class {@code owner}
     */
    @NotNull
    public static Method findAccessible(final @NotNull Class<?> owner, final @NotNull String name, final @Nullable Class<?>... parameterTypes) {
        final Method method = Methods.find(owner, name, parameterTypes);
        AccessibleObjects.setAccessible(method);
        return method;
    }

    /**
     * Retrieves the {@link Method} with the given {@code name} and parameter types declared by class {@code owner}.
     *
     * @param owner          The class from which the method is accessed
     * @param name           The name of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the {@link Method} with the given name and parameter types declared by class {@code owner}
     */
    @NotNull
    public static Method find(final @NotNull Class<?> owner, final @NotNull String name, final @Nullable Class<?>... parameterTypes) {
        return ThrowingExecutable.execute(
                () -> owner.getDeclaredMethod(name, parameterTypes)
        );
    }

    /**
     * Retrieves the {@linkplain Method#getRoot() root Method} with the given {@code name} and parameter types declared by class {@code owner}
     * and makes it accessible. This method will disable any reflection filters set on methods declared by {@code owner}
     * before retrieving the method.
     *
     * @param owner          The class from which the method is accessed
     * @param name           The name of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the {@link Method} with the given name and parameter types declared by class {@code owner}
     */
    @NotNull
    public static Method unfilterAndFindAccessibleRoot(final @NotNull Class<?> owner, final @NotNull String name, final @Nullable Class<?>... parameterTypes) {
        Methods.unfilter(owner);
        return Methods.findAccessibleRoot(owner, name, parameterTypes);
    }

    /**
     * Retrieves the {@linkplain Method#getRoot() root Method} with the given {@code name} and parameter types declared by class {@code owner}.
     * This method will disable any reflection filters set on methods declared by {@code owner}
     * before retrieving the method.
     *
     * @param owner          The class from which the method is accessed
     * @param name           The name of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the {@link Method} with the given name and parameter types declared by class {@code owner}
     */
    @NotNull
    public static Method unfilterAndFindRoot(final @NotNull Class<?> owner, final @NotNull String name, final @Nullable Class<?>... parameterTypes) {
        Methods.unfilter(owner);
        return Methods.findRoot(owner, name, parameterTypes);
    }

    /**
     * Retrieves the {@linkplain Method#getRoot() root Method} with the given {@code name} and parameter types declared by class {@code owner}
     * and makes it accessible.
     *
     * @param owner          The class from which the method is accessed
     * @param name           The name of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the {@link Method} with the given name and parameter types declared by class {@code owner}
     */
    @NotNull
    public static Method findAccessibleRoot(final @NotNull Class<?> owner, final @NotNull String name, final @Nullable Class<?>... parameterTypes) {
        final Method method = Methods.findRoot(owner, name, parameterTypes);
        AccessibleObjects.setAccessible(method);
        return method;
    }

    /**
     * Retrieves the {@linkplain Method#getRoot() root Method} with the given {@code name} and parameter types declared by class {@code owner}.
     *
     * @param owner          The class from which the method is accessed
     * @param name           The name of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the {@link Method} with the given name and parameter types declared by class {@code owner}
     */
    @NotNull
    public static Method findRoot(final @NotNull Class<?> owner, final @NotNull String name, final @Nullable Class<?>... parameterTypes) {
        // noinspection DataFlowIssue
        return AccessibleObjects.getRoot(Methods.find(owner, name, parameterTypes));
    }

    /**
     * Retrieves the {@link Method} with the given {@code name} and parameter types declared by class {@code owner}
     * and makes it accessible.
     * <p>
     * This method requests the method directly from the VM. This means that:
     * <ul>
     *     <li>
     *         this method is not affected by reflection filters
     *     </li>
     *     <li>
     *         this method is independent from {@code owner}'s cached reflection data
     *     </li>
     *     <li>
     *         the returned method will be a {@linkplain Method#getRoot()} root Method}
     *      </li>
     * </ul>
     *
     * @param owner          The class from which the method is accessed
     * @param name           The name of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the {@link Method} with the given {@code name} and parameter types declared by class {@code owner}
     */
    @NotNull
    public static Method findDirectAccessible(final @NotNull Class<?> owner, final @NotNull String name, final @Nullable Class<?>... parameterTypes) {
        final Method method = Methods.findDirect(owner, name, parameterTypes);
        AccessibleObjects.setAccessible(method);
        return method;
    }

    /**
     * Retrieves the {@link Method} with the given {@code name} and parameter types declared by class {@code owner}.
     * <p>
     * This method requests the method directly from the VM. This means that:
     * <ul>
     *     <li>
     *         this method is not affected by reflection filters
     *     </li>
     *     <li>
     *         this method is independent from {@code owner}'s cached reflection data
     *     </li>
     *     <li>
     *         the returned method will be a {@linkplain Method#getRoot()} root Method}
     *      </li>
     * </ul>
     *
     * @param owner          The class from which the method is accessed
     * @param name           The name of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the {@link Method} with the given {@code name} and parameter types declared by class {@code owner}
     */
    @NotNull
    public static Method findDirect(final @NotNull Class<?> owner, final @NotNull String name, final @Nullable Class<?>... parameterTypes) {
        final Method[] methods = Methods.getDirect(owner);

        for (final Method method : methods) {
            if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes)) {
                return method;
            }
        }

        throw Throwables.sneakyThrow(Methods.newNoSuchMethodException(owner, name, parameterTypes));
    }

    /**
     * Retrieves the {@link Method}(s) with the given {@code name} declared by class {@code owner}
     * and makes them accessible. This method will disable any reflection filters set on methods declared
     * by {@code owner} before retrieving the methods.
     *
     * @param owner The class from which the methods are accessed
     * @param name  The name of the method(s) to retrieve
     * @return an array containing the {@link Method}(s) with the given {@code name} declared by class {@code owner}
     * @see Methods#unfilterAndGetAccessible(Class)
     * @see Methods#select(Method[], String)
     */
    public static @NotNull Method @NotNull [] unfilterAndGetAccessibleWithName(final @NotNull Class<?> owner, final @NotNull String name) {
        return Methods.select(Methods.unfilterAndGetAccessible(owner), name);
    }

    /**
     * Retrieves the {@link Method}(s) with the given {@code name} declared by class {@code owner}.
     * This method will disable any reflection filters set on methods declared by {@code owner}
     * before retrieving the methods.
     *
     * @param owner The class from which the methods are accessed
     * @param name  The name of the method(s) to retrieve
     * @return an array containing the {@link Method}(s) with the given {@code name} declared by class {@code owner}
     * @see Methods#unfilterAndGet(Class)
     * @see Methods#select(Method[], String)
     */
    public static @NotNull Method @NotNull [] unfilterAndGetWithName(final @NotNull Class<?> owner, final @NotNull String name) {
        return Methods.select(Methods.unfilterAndGet(owner), name);
    }

    /**
     * Retrieves the {@link Method}s declared by class {@code owner} and makes them accessible.
     * This method will disable any reflection filters set on methods declared by {@code owner}
     * before retrieving the methods.
     *
     * @param owner The class from which the methods are accessed
     * @return an array containing the {@link Method}s declared by {@code owner}
     * @see Methods#unfilter(Class)
     * @see Methods#getAccessible(Class)
     */
    public static @NotNull Method @NotNull [] unfilterAndGetAccessible(final @NotNull Class<?> owner) {
        Methods.unfilter(owner);
        return Methods.getAccessible(owner);
    }

    /**
     * Retrieves the {@link Method}s declared by class {@code owner}.
     * This method will disable any reflection filters set on methods declared by {@code owner}
     * before retrieving the methods.
     *
     * @param owner The class from which the methods are accessed
     * @return an array containing the {@link Method}s declared by {@code owner}
     * @see Methods#unfilter(Class)
     */
    public static @NotNull Method @NotNull [] unfilterAndGet(final @NotNull Class<?> owner) {
        Methods.unfilter(owner);
        return owner.getDeclaredMethods();
    }

    /**
     * Retrieves the {@link Method}(s) with the given {@code name} declared by class {@code owner}
     * and makes them accessible.
     *
     * @param owner The class from which the methods are accessed
     * @param name  The name of the method(s) to retrieve
     * @return an array containing the {@link Method}(s) with the given {@code name} declared by class {@code owner}
     * @see Methods#select(Method[], String)
     * @see Methods#getAccessible(Class)
     */
    public static @NotNull Method @NotNull [] getAccessibleWithName(final @NotNull Class<?> owner, final @NotNull String name) {
        return Methods.select(Methods.getAccessible(owner), name);
    }

    /**
     * Retrieves the {@link Method}s of class {@code owner} and makes them accessible.
     *
     * @param owner The class from which the methods are accessed
     * @return an array containing the {@link Method}s declared by {@code owner}
     * @see AccessibleObjects#setAccessible(AccessibleObject[])
     */
    public static @NotNull Method @NotNull [] getAccessible(final @NotNull Class<?> owner) {
        final Method[] methods = owner.getDeclaredMethods();
        AccessibleObjects.setAccessible(methods);

        return methods;
    }

    /**
     * Retrieves the {@link Method}(s) with the given {@code name} declared by class {@code owner}.
     * <p>
     * This method requests the methods directly from the VM. This means that:
     * <ul>
     *     <li>
     *         this method is not affected by reflection filters
     *     </li>
     *     <li>
     *         this method is independent from {@code owner}'s cached reflection data
     *     </li>
     *     <li>
     *         the returned array will contain {@linkplain Method#getRoot()} root Methods}
     *      </li>
     * </ul>
     *
     * @param owner The class from which the methods are accessed
     * @return an array containing the {@link Method}(s) with the given {@code name} declared by class {@code owner}
     * @see Methods#select(Method[], String)
     * @see Methods#getDirect(Class)
     */
    public static @NotNull Method @NotNull [] getDirectWithName(final @NotNull Class<?> owner, final @NotNull String name) {
        return Methods.select(Methods.getDirect(owner), name);
    }

    /**
     * Retrieves the {@link Method}s with the given {@code name} declared by class {@code owner} and makes them accessible.
     * <p>
     * This method requests the methods directly from the VM. This means that:
     * <ul>
     *     <li>
     *         this method is not affected by reflection filters
     *     </li>
     *     <li>
     *         this method is independent from {@code owner}'s cached reflection data
     *     </li>
     *     <li>
     *         the returned array will contain {@linkplain Method#getRoot()} root Methods}
     *      </li>
     * </ul>
     *
     * @param owner The class from which the methods are accessed
     * @return an array containing the {@link Method}s with the given {@code name} declared by class {@code owner}
     * @see Methods#select(Method[], String)
     * @see Methods#getDirectAccessible(Class)
     */
    public static @NotNull Method @NotNull [] getDirectAccessibleWithName(final @NotNull Class<?> owner, final @NotNull String name) {
        return Methods.select(Methods.getDirectAccessible(owner), name);
    }

    /**
     * Retrieves the {@link Method}s declared by class {@code owner} and makes them accessible. <br>
     * This method requests the methods directly from the VM. This means that:
     * <ul>
     *     <li>
     *         this method is not affected by reflection filters
     *     </li>
     *     <li>
     *         this method is independent from {@code owner}'s cached reflection data
     *     </li>
     *     <li>
     *         the returned array will contain {@linkplain Method#getRoot()} root Methods}
     *      </li>
     * </ul>
     *
     * @param owner The class from which the methods are accessed
     * @return an array containing the {@link Method}s declared by class {@code owner}
     * @see Methods#getDirect(Class)
     * @see AccessibleObjects#setAccessible(AccessibleObject[])
     */
    public static @NotNull Method @NotNull [] getDirectAccessible(final @NotNull Class<?> owner) {
        final Method[] methods = Methods.getDirect(owner);
        AccessibleObjects.setAccessible(methods);

        return methods;
    }

    /**
     * Retrieves the {@link Method}s declared by class {@code owner}. <br>
     * This method requests the methods directly from the VM. This means that:
     * <ul>
     *     <li>
     *         this method is not affected by reflection filters
     *     </li>
     *     <li>
     *         this method is independent from {@code owner}'s cached reflection data
     *     </li>
     *     <li>
     *         the returned array will contain {@linkplain Method#getRoot()} root Methods}
     *      </li>
     * </ul>
     *
     * @param owner The class from which the methods are accessed
     * @return an array containing the {@link Method}s declared by class {@code owner}
     */
    public static @NotNull Method @NotNull [] getDirect(final @NotNull Class<?> owner) {
        // noinspection DataFlowIssue
        return Handles.invoke(NATIVE_GET_DECLARED_METHODS_MH, owner);
    }

    /**
     * Disables reflection filters set on methods declared by class {@code owner}
     * and clears {@code owner}'s cached reflection data.
     *
     * @param owner The class for which the filters will be disabled
     * @see Reflection#unregisterMethodsToFilter(Class)
     * @see Reflection#clearReflectionCache(Class)
     */
    public static void unfilter(final @NotNull Class<?> owner) {
        Reflection.unregisterMethodsToFilter(owner);
        Reflection.clearReflectionCache(owner);
    }

    /**
     * Selects the {@link Method}s with the given {@code name} from the given array.
     *
     * @param methods The methods to select from
     * @param name    The name of the methods to select
     * @return a new array containing the methods from the original array with the given {@code name}
     */
    public static @NotNull Method @NotNull [] select(final @NotNull Method @NotNull [] methods, final @NotNull String name) {
        if (methods.length == 0) {
            return methods;
        }

        final List<Method> matching = new ArrayList<>();

        for (final Method method : methods) {
            if (method.getName().equals(name)) {
                matching.add(method);
            }
        }

        return matching.toArray(new Method[0]);
    }

    /**
     * Constructs a {@link NoSuchMethodException} indicating that a method with the given {@code name} and parameter types
     * could not be found in class {@code owner}.
     *
     * @param owner          The class from which the method should have been accessed
     * @param name           The name of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the created {@link NoSuchMethodException}
     */
    @NotNull
    private static NoSuchMethodException newNoSuchMethodException(final @NotNull Class<?> owner, final @NotNull String name, final @Nullable Class<?>... parameterTypes) {
        final StringBuilder message = new StringBuilder(owner.getName()).append('.').append(name).append('(');

        if (parameterTypes != null) {
            for (int parameterIndex = 0; parameterIndex < parameterTypes.length; parameterIndex++) {

                if (parameterIndex != 0) {
                    message.append(", ");
                }

                final Class<?> parameterType;

                message.append(
                        (parameterType = parameterTypes[parameterIndex]) == null ?
                                null :
                                parameterType.getName()
                );
            }
        }

        message.append(')');

        return new NoSuchMethodException(message.toString());
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Methods() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(Methods.class) + " cannot be instantiated");
    }

}
