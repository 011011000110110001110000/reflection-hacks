package reflection.hacks.api.reflect;

import org.jetbrains.annotations.NotNull;
import reflection.hacks.api.invoke.Handles;
import reflection.hacks.internal.util.CompatHelper;
import reflection.hacks.internal.util.Lazy;
import reflection.hacks.internal.util.Throwables;
import reflection.hacks.internal.util.function.ThrowingExecutable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

/**
 * This class provides an API to retrieve the {@link Field}s declared by a class.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @since 1.0
 */
public final class Fields {

    /**
     * Lazily cached {@link MethodHandle} for the native method that retrieves the declared {@link Field}s of a {@link Class}
     */
    @NotNull
    private static final Lazy<MethodHandle> NATIVE_GET_DECLARED_FIELDS_MH;

    static {

        NATIVE_GET_DECLARED_FIELDS_MH = Lazy.of(CompatHelper::nativeDeclaredFieldRetriever);

    }

    /**
     * Retrieves the {@linkplain Field#getRoot() root Field} with the given {@code name} and parameter types declared by class {@code owner}
     * and makes it accessible.
     *
     * @param owner The class from which the field is accessed
     * @param name  The name of the field
     * @return the {@link Field} with the given {@code name} declared by class {@code owner}
     */
    @NotNull
    public static Field findAccessibleRoot(final @NotNull Class<?> owner, final @NotNull String name) {
        final Field field = Fields.findRoot(owner, name);
        AccessibleObjects.setAccessible(field);
        return field;
    }

    /**
     * Retrieves the {@linkplain Field#getRoot() root Field} with the given {@code name} and parameter types declared by class {@code owner}
     * and makes it accessible. This method will disable any reflection filters set on fields declared by {@code owner}
     * before retrieving the field.
     *
     * @param owner The class from which the field is accessed
     * @param name  The name of the field
     * @return the {@link Field} with the given {@code name} declared by class {@code owner}
     */
    @NotNull
    public static Field unfilterAndFindAccessibleRoot(final @NotNull Class<?> owner, final @NotNull String name) {
        Fields.unfilter(owner);
        return Fields.findAccessibleRoot(owner, name);
    }

    /**
     * Retrieves the {@linkplain Field#getRoot() root Field} with the given {@code name} and parameter types declared by class {@code owner}.
     * This method will disable any reflection filters set on fields declared by {@code owner} before retrieving the field.
     *
     * @param owner The class from which the field is accessed
     * @param name  The name of the field
     * @return the {@link Field} with the given {@code name} declared by class {@code owner}
     */
    @NotNull
    public static Field unfilterAndFindRoot(final @NotNull Class<?> owner, final @NotNull String name) {
        Fields.unfilter(owner);
        return Fields.findRoot(owner, name);
    }

    /**
     * Retrieves the {@linkplain Field#getRoot() root Field} with the given {@code name} and parameter types declared by class {@code owner}.
     *
     * @param owner The class from which the field is accessed
     * @param name  The name of the field
     * @return the {@link Field} with the given {@code name} declared by class {@code owner}
     */
    @NotNull
    public static Field findRoot(final @NotNull Class<?> owner, final @NotNull String name) {
        // noinspection DataFlowIssue
        return AccessibleObjects.getRoot(Fields.find(owner, name));
    }

    /**
     * Retrieves the {@link Field} with the given {@code name} and parameter types declared by class {@code owner}
     * and makes it accessible.
     *
     * @param owner The class from which the field is accessed
     * @param name  The name of the field
     * @return the {@link Field} with the given {@code name} declared by class {@code owner}
     */
    @NotNull
    public static Field findAccessible(final @NotNull Class<?> owner, final @NotNull String name) {
        final Field field = Fields.find(owner, name);
        AccessibleObjects.setAccessible(field);
        return field;
    }

    /**
     * Retrieves the {@link Field} with the given {@code name} and parameter types declared by class {@code owner}.
     * This method will disable any reflection filters set on fields declared by {@code owner} before retrieving the field.
     *
     * @param owner The class from which the field is accessed
     * @param name  The name of the field
     * @return the {@link Field} with the given {@code name} declared by class {@code owner}
     */
    @NotNull
    public static Field unfilterAndFind(final @NotNull Class<?> owner, final @NotNull String name) {
        Fields.unfilter(owner);
        return Fields.find(owner, name);
    }

    /**
     * Retrieves the {@link Field} with the given {@code name} and parameter types declared by class {@code owner}
     * and makes it accessible. This method will disable any reflection filters set on fields declared by {@code owner}
     * before retrieving the field.
     *
     * @param owner The class from which the field is accessed
     * @param name  The name of the field
     * @return the {@link Field} with the given {@code name} declared by class {@code owner}
     */
    @NotNull
    public static Field unfilterAndFindAccessible(final @NotNull Class<?> owner, final @NotNull String name) {
        Fields.unfilter(owner);
        return Fields.findAccessible(owner, name);
    }

    /**
     * Retrieves the {@link Field} with the given {@code name} and parameter types declared by class {@code owner}.
     *
     * @param owner The class from which the field is accessed
     * @param name  The name of the field
     * @return the {@link Field} with the given {@code name} declared by class {@code owner}
     */
    @NotNull
    public static Field find(final @NotNull Class<?> owner, final @NotNull String name) {
        return ThrowingExecutable.execute(
                () -> owner.getDeclaredField(name)
        );
    }

    /**
     * Retrieves the {@link Field} with the given {@code name} declared by class {@code owner}
     * and makes it accessible.
     * <p>
     * This method requests the field directly from the VM. This means that:
     * <ul>
     *     <li>
     *         this method is not affected by reflection filters
     *     </li>
     *     <li>
     *         this method is independent from {@code owner}'s cached reflection data
     *     </li>
     *     <li>
     *         the returned field will be a {@linkplain Field#getRoot()} root Field}
     *      </li>
     * </ul>
     *
     * @param owner The class from which the field is accessed
     * @param name  The name of the field
     * @return the {@link Field} with the given {@code name} declared by class {@code owner}
     */
    @NotNull
    public static Field findDirectAccessible(final @NotNull Class<?> owner, final @NotNull String name) {
        final Field field = Fields.findDirect(owner, name);
        AccessibleObjects.setAccessible(field);
        return field;
    }

    /**
     * Retrieves the {@link Field} with the given {@code name} declared by class {@code owner}.
     * <p>
     * This method requests the field directly from the VM. This means that:
     * <ul>
     *     <li>
     *         this method is not affected by reflection filters
     *     </li>
     *     <li>
     *         this method is independent from {@code owner}'s cached reflection data
     *     </li>
     *     <li>
     *         the returned field will be a {@linkplain Field#getRoot()} root Field}
     *      </li>
     * </ul>
     *
     * @param owner The class from which the field is accessed
     * @param name  The name of the field
     * @return the {@link Field} with the given {@code name} and parameter types declared by class {@code owner}
     */
    @NotNull
    public static Field findDirect(final @NotNull Class<?> owner, final @NotNull String name) {
        final Field[] fields = Fields.getDirect(owner);

        for (final Field field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        throw Throwables.sneakyThrow(new NoSuchFieldException(owner.getSimpleName() + "." + name));
    }

    /**
     * Retrieves the {@link Field}s declared by class {@code owner} and makes them accessible. This method will disable
     * any reflection filters set on fields declared by {@code owner} before retrieving the fields.
     *
     * @param owner The class from which the fields are accessed
     * @return an array containing the {@link Field}s declared by class {@code owner}
     * @see Fields#unfilter(Class)
     * @see AccessibleObjects#setAccessible(AccessibleObject[])
     */
    public static @NotNull Field @NotNull [] getAccessible(final @NotNull Class<?> owner) {
        final Field[] fields = owner.getDeclaredFields();
        AccessibleObjects.setAccessible(fields);
        return fields;
    }

    /**
     * Retrieves the {@link Field}s declared by class {@code owner} and makes them accessible. This method will disable
     * any reflection filters set on fields declared by {@code owner} before retrieving the fields.
     *
     * @param owner The class from which the fields are accessed
     * @return an array containing the {@link Field}s declared by class {@code owner}
     * @see Fields#unfilter(Class)
     * @see Fields#getAccessible(Class)
     */
    public static @NotNull Field @NotNull [] unfilterAndGetAccessible(final @NotNull Class<?> owner) {
        Fields.unfilter(owner);
        return Fields.getAccessible(owner);
    }

    /**
     * Retrieves the {@link Field}s declared by class {@code owner}. This method will disable
     * any reflection filters set on fields declared by {@code owner} before retrieving the fields.
     *
     * @param owner The class from which the fields are accessed
     * @return an array containing the {@link Field}s declared by class {@code owner}
     * @see Fields#unfilter(Class)
     */
    public static @NotNull Field @NotNull [] unfilterAndGet(final @NotNull Class<?> owner) {
        Fields.unfilter(owner);
        return owner.getDeclaredFields();
    }

    /**
     * Retrieves the {@link Field}s declared by class {@code owner}. <br>
     * This method requests the fields directly from the VM. This means that:
     * <ul>
     *     <li>
     *         this method is not affected by reflection filters
     *     </li>
     *     <li>
     *         this method is independent from {@code owner}'s cached reflection data
     *     </li>
     *     <li>
     *         the array will contain {@linkplain Field#root root Fields}
     *      </li>
     * </ul>
     *
     * @param owner The class from which the fields are accessed
     * @return an array containing the {@link Field}s declared by class {@code owner}
     */
    public static @NotNull Field @NotNull [] getDirect(final @NotNull Class<?> owner) {
        return Handles.invoke(Fields.NATIVE_GET_DECLARED_FIELDS_MH.get(), owner);
    }

    /**
     * Disables reflection filters set on fields declared by class {@code owner}
     * and clears {@code owner}'s cached reflection data.
     *
     * @param owner The class for which the filters will be disabled
     * @see Reflection#unregisterFieldsToFilter(Class)
     * @see Reflection#clearReflectionCache(Class)
     */
    public static void unfilter(final @NotNull Class<?> owner) {
        Reflection.unregisterFieldsToFilter(owner);
        Reflection.clearReflectionCache(owner);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Fields() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(Fields.class) + " cannot be instantiated");
    }

}
