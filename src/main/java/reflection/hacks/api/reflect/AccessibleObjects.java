package reflection.hacks.api.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reflection.hacks.api.invoke.Handles;
import reflection.hacks.internal.util.Lazy;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AccessibleObject;
import java.util.Collection;
import java.util.stream.Stream;

public final class AccessibleObjects {

    /**
     * Lazily cached {@link MethodHandle} with {@code invokespecial} behavior for {@link AccessibleObject#setAccessible(boolean)}
     *
     * @see AccessibleObjects#setAccessible(boolean, AccessibleObject)
     */
    @NotNull
    private static final Lazy<MethodHandle> SET_ACCESSIBLE_MH;

    /**
     * Lazily cached {@link MethodHandle} for {@link AccessibleObject#getRoot()}
     *
     * @see AccessibleObjects#getRoot(AccessibleObject)
     */
    @NotNull
    private static final Lazy<MethodHandle> GET_ROOT_MH;

    static {

        SET_ACCESSIBLE_MH = Lazy.of(
                () -> Handles.findSpecial(
                        AccessibleObject.class,
                        "setAccessible",
                        AccessibleObject.class,
                        void.class,
                        boolean.class
                )
        );

        GET_ROOT_MH = Lazy.of(
                () -> Handles.findVirtual(
                        AccessibleObject.class,
                        "getRoot",
                        AccessibleObject.class
                )
        );

    }

    /**
     * Forces the given {@link AccessibleObject}s to be accessible.
     *
     * @param objects The objects to be made accessible
     * @see AccessibleObjects#setAccessible(boolean, Stream)
     */
    public static void setAccessible(final @NotNull Stream<? extends @NotNull AccessibleObject> objects) {
        AccessibleObjects.setAccessible(true, objects);
    }

    /**
     * Forces the given {@link AccessibleObject}s to have the desired accessibility
     *
     * @param accessible The accessibility to be forcefully set
     * @param objects    The objects whose accessibility is to be forcefully set
     * @see AccessibleObjects#setAccessible(boolean, AccessibleObject)
     */
    public static void setAccessible(final boolean accessible, final @NotNull Stream<? extends @NotNull AccessibleObject> objects) {
        objects.forEach(
                object -> AccessibleObjects.setAccessible(accessible, object)
        );
    }

    /**
     * Forces the given {@link AccessibleObject}s to be accessible.
     *
     * @param objects The objects to be made accessible
     * @see AccessibleObjects#setAccessible(boolean, Collection)
     */
    public static void setAccessible(final @NotNull Collection<? extends @NotNull AccessibleObject> objects) {
        AccessibleObjects.setAccessible(true, objects);
    }

    /**
     * Forces the given {@link AccessibleObject}s to have the desired accessibility
     *
     * @param accessible The accessibility to be forcefully set
     * @param objects    The objects whose accessibility is to be forcefully set
     * @see AccessibleObjects#setAccessible(boolean, AccessibleObject)
     */
    public static void setAccessible(final boolean accessible, final @NotNull Collection<? extends @NotNull AccessibleObject> objects) {
        objects.forEach(
                object -> AccessibleObjects.setAccessible(accessible, object)
        );
    }

    /**
     * Forces the given {@link AccessibleObject}s to be accessible.
     *
     * @param objects The objects to be made accessible
     * @see AccessibleObjects#setAccessible(boolean, AccessibleObject...)
     */
    public static void setAccessible(final @NotNull AccessibleObject @NotNull ... objects) {
        AccessibleObjects.setAccessible(true, objects);
    }

    /**
     * Forces the given {@link AccessibleObject}s to have the desired accessibility
     *
     * @param accessible The accessibility to be forcefully set
     * @param objects    The objects whose accessibility is to be forcefully set
     * @see AccessibleObjects#setAccessible(boolean, AccessibleObject)
     */
    public static void setAccessible(final boolean accessible, final @NotNull AccessibleObject @NotNull ... objects) {
        for (AccessibleObject object : objects) {
            AccessibleObjects.setAccessible(accessible, object);
        }
    }

    /**
     * Forces the given {@link AccessibleObject} to be accessible.
     *
     * @param object The object to be made accessible
     * @see AccessibleObjects#setAccessible(boolean, AccessibleObject)
     */
    public static void setAccessible(final @NotNull AccessibleObject object) {
        AccessibleObjects.setAccessible(true, object);
    }

    /**
     * Forces the given {@link AccessibleObject} instance to have the desired accessibility by (ab)using {@code invokespecial} behavior,
     * bypassing access checks.
     *
     * @param accessible The accessibility to be forcefully set
     * @param object     The object whose accessibility is to be forcefully set
     * @see AccessibleObject#setAccessible(boolean)
     */
    public static void setAccessible(final boolean accessible, final @NotNull AccessibleObject object) {
        Handles.invoke(AccessibleObjects.SET_ACCESSIBLE_MH.get(), object, accessible);
    }

    /**
     * Retrieves the root {@link AccessibleObject} for the given {@code object}.
     *
     * @param object The {@link AccessibleObject} to get the root of
     * @return the root of the given {@link AccessibleObject}, or {@code null} if {@code object} is the root
     * @see AccessibleObject#getRoot()
     */
    @Nullable
    public static <T extends AccessibleObject> T getRoot(final @NotNull T object) {
        return Handles.invoke(AccessibleObjects.GET_ROOT_MH.get(), object);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private AccessibleObjects() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(AccessibleObjects.class) + " cannot be instantiated");
    }

}
