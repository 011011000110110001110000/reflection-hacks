package reflection.hacks.api.reflect;

import org.jetbrains.annotations.NotNull;
import reflection.hacks.api.invoke.Handles;
import reflection.hacks.internal.util.CompatHelper;
import reflection.hacks.internal.util.function.ThrowingExecutable;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class provides an API to interact with the internal {@link jdk.internal.reflect.Reflection} class
 * as well as other related utilities.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @since 1.0
 */
public final class Reflection {

    /**
     * Cached {@link StackWalker} instance with caller checking capability
     */
    public static final StackWalker STACK_WALKER;

    /**
     * Cached {@link VarHandle} for the cached reflection data of a {@link Class}
     *
     * @see #clearReflectionCache(Class)
     */
    private static final VarHandle REFLECTION_CACHE;

    /**
     * Cached {@link VarHandle} for {@link jdk.internal.reflect.Reflection#fieldFilterMap}
     *
     * @see #unregisterFilters(Class)
     */
    private static final VarHandle FIELD_FILTER_MAP;

    /**
     * Cached {@link VarHandle} for {@link jdk.internal.reflect.Reflection#methodFilterMap}
     *
     * @see #unregisterFilters(Class)
     */
    private static final VarHandle METHOD_FILTER_MAP;

    static {

        STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

        REFLECTION_CACHE = CompatHelper.reflectionCacheHandle();

        final String reflectionClassName = "jdk.internal.reflect.Reflection";
        final String fieldFilterMap = "fieldFilterMap";
        final String methodFilterMap = "methodFilterMap";

        // noinspection Java9ReflectionClassVisibility
        final Class<?> reflectionClass = ThrowingExecutable.execute(
                () -> Class.forName(reflectionClassName)
        );


        FIELD_FILTER_MAP = Handles.findStaticVarHandle(reflectionClass, fieldFilterMap, Map.class);
        METHOD_FILTER_MAP = Handles.findStaticVarHandle(reflectionClass, methodFilterMap, Map.class);

    }

    /**
     * Ensures any reflective operations on the given class will not be affected by reflection filters.
     *
     * @param clazz The class on which reflective operations will be performed
     * @see Reflection#unregisterFilters(Class)
     * @see Reflection#clearReflectionCache(Class)
     */
    public static void ensureUnfilteredReflection(final @NotNull Class<?> clazz) {
        Reflection.unregisterFilters(clazz);
        Reflection.clearReflectionCache(clazz);
    }

    /**
     * Clears the cached reflection data for the given class. <br>
     * This is only really useful after {@link Reflection#unregisterFilters(Class, boolean, boolean) removing reflection filters} for {@code clazz}.
     *
     * @param clazz The class whose reflection cache is to be cleared
     */
    public static void clearReflectionCache(final @NotNull Class<?> clazz) {
        // noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (clazz) {
            Reflection.REFLECTION_CACHE.set(clazz, null);
        }
    }

    /**
     * Removes any reflection filters that were set for fields in the given class.
     *
     * @param clazz The class for which the filters are to be removed
     * @see Reflection#unregisterFilters(Class, boolean, boolean)
     * @see jdk.internal.reflect.Reflection#registerFieldsToFilter(Class, Set)
     * @see jdk.internal.reflect.Reflection#filterFields(Class, Field[])
     */
    public static void unregisterFieldsToFilter(final @NotNull Class<?> clazz) {
        Reflection.unregisterFilters(clazz, true, false);
    }

    /**
     * Removes any reflection filters that were set for methods in the given class.
     *
     * @param clazz The class for which the filters are to be removed
     * @see Reflection#unregisterFilters(Class, boolean, boolean)
     * @see jdk.internal.reflect.Reflection#registerMethodsToFilter(Class, Set)
     * @see jdk.internal.reflect.Reflection#filterMethods(Class, Method[])
     */
    public static void unregisterMethodsToFilter(final @NotNull Class<?> clazz) {
        Reflection.unregisterFilters(clazz, false, true);
    }

    /**
     * Removes all reflection filters that were set for the given class.
     *
     * @param clazz The class for which the filters are to be removed
     * @see Reflection#unregisterFilters(Class, boolean, boolean)
     */
    public static void unregisterFilters(final @NotNull Class<?> clazz) {
        Reflection.unregisterFilters(clazz, true, true);
    }

    /**
     * Removes the desired reflection filters that were set for the given class.
     *
     * @param clazz        The class for which the filters should be removed
     * @param clearFields  Whether reflection filters on fields should be cleared
     * @param clearMethods Whether reflection filters on methods should be cleared
     * @see jdk.internal.reflect.Reflection#registerFieldsToFilter(Class, Set)
     * @see jdk.internal.reflect.Reflection#filterFields(Class, Field[])
     * @see jdk.internal.reflect.Reflection#registerMethodsToFilter(Class, Set)
     * @see jdk.internal.reflect.Reflection#filterMethods(Class, Method[])
     */
    public static void unregisterFilters(final @NotNull Class<?> clazz, final boolean clearFields, final boolean clearMethods) {
        if (clearFields) {
            final Map<Class<?>, Set<String>> originalFieldFilterMap = getFieldFilterMap();
            final Map<Class<?>, Set<String>> newFieldFilterMap;

            // noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (clazz) {
                if (originalFieldFilterMap.containsKey(clazz)) {
                    newFieldFilterMap = new HashMap<>(originalFieldFilterMap);
                    newFieldFilterMap.remove(clazz);
                    Reflection.FIELD_FILTER_MAP.setVolatile(newFieldFilterMap);
                }
            }
        }

        if (clearMethods) {
            final Map<Class<?>, Set<String>> originalMethodFilterMap = getMethodFilterMap();
            final Map<Class<?>, Set<String>> newMethodFilterMap;

            // noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (clazz) {
                if (originalMethodFilterMap.containsKey(clazz)) {
                    newMethodFilterMap = new HashMap<>(originalMethodFilterMap);
                    newMethodFilterMap.remove(clazz);
                    Reflection.METHOD_FILTER_MAP.setVolatile(newMethodFilterMap);
                }
            }
        }
    }

    /**
     * Getter for {@link jdk.internal.reflect.Reflection#fieldFilterMap}.
     *
     * @return a reference to the {@code fieldFilterMap}
     */
    public static Map<Class<?>, Set<String>> getFieldFilterMap() {
        return Classes.unchecked(Reflection.FIELD_FILTER_MAP.getVolatile());
    }

    /**
     * Getter for {@link jdk.internal.reflect.Reflection#methodFilterMap}.
     *
     * @return a reference to the {@code methodFilterMap}
     */
    public static Map<Class<?>, Set<String>> getMethodFilterMap() {
        return Classes.unchecked(Reflection.METHOD_FILTER_MAP.getVolatile());
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Reflection() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(Reflection.class) + " cannot be instantiated");
    }


}
