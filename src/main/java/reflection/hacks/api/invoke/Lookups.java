package reflection.hacks.api.invoke;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reflection.hacks.api.reflect.Classes;
import reflection.hacks.internal.access.JavaLangAccessBridge;
import reflection.hacks.internal.util.function.ThrowingExecutable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;

/**
 * This class provides an API for obtaining {@link Lookup} instances with arbitrary access capabilities. <br>
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @since 1.0
 */
public final class Lookups {

    /**
     * Cached {@link MethodHandle} for the {@link MethodHandles.Lookup} constructor
     *
     * @see Lookups#newLookup(Class, Class, int)
     */
    @NotNull
    private static final MethodHandle LOOKUP_CONSTRUCTOR_MH;

    /**
     * Integer flag indicating that the lookup object has trusted access
     */
    private static final int TRUSTED_ACCESS_MODES;

    /**
     * Integer flag indicating that the lookup object has full privilege access
     */
    private static final int FULL_PRIVILEGE_ACCESS_MODES;

    /**
     * Cached {@link MethodHandles.Lookup} instance which is trusted
     */
    @NotNull
    static final Lookup LOOKUP;

    static {

        TRUSTED_ACCESS_MODES = -1;
        FULL_PRIVILEGE_ACCESS_MODES = MethodHandles.lookup().lookupModes();

        // Enable AccessibleObject#setAccessible(boolean) usage on the MethodHandles.Lookup members
        JavaLangAccessBridge.addOpens(Object.class.getModule(), "java.lang.invoke", Lookups.class.getModule());

        LOOKUP_CONSTRUCTOR_MH = ThrowingExecutable.execute(
                () -> {
                    final Constructor<MethodHandles.Lookup> lookupConstructor = MethodHandles.Lookup.class
                            .getDeclaredConstructor(
                                    Class.class,
                                    Class.class,
                                    int.class
                            );
                    lookupConstructor.setAccessible(true);
                    return MethodHandles.lookup().unreflectConstructor(lookupConstructor);
                }
        );

        LOOKUP = Lookups.trustedLookupIn(Object.class);

    }

    /**
     * Produces a {@link MethodHandles.Lookup} instance with the given {@code lookupClass},
     * as if obtained via a call to {@link MethodHandles#lookup()} by {@code lookupClass} itself.
     * As such, the lookup object will have full privilege access.
     *
     * @param lookupClass The desired lookup class
     * @return the created {@code Lookup} object
     */
    @NotNull
    public static MethodHandles.Lookup lookupIn(final @NotNull Class<?> lookupClass) {
        return Lookups.newLookup(lookupClass, null, FULL_PRIVILEGE_ACCESS_MODES);
    }

    /**
     * Produces a trusted {@link MethodHandles.Lookup} instance with the given {@code lookupClass} and a {@code null} {@code previousLookupClass}.
     *
     * @param lookupClass The desired lookup class
     * @return the created {@code Lookup} object
     */
    @NotNull
    public static MethodHandles.Lookup trustedLookupIn(final @NotNull Class<?> lookupClass) {
        return Lookups.newLookup(lookupClass, null, TRUSTED_ACCESS_MODES);
    }

    /**
     * Produces a {@link MethodHandles.Lookup} instance with the given {@code lookupClass},
     * {@code previousLookupClass}, and {@code lookupModes}.
     *
     * @param lookupClass         The desired lookup class
     * @param previousLookupClass The desired previous lookup class
     * @param lookupModes         The desired lookup modes
     * @return the created {@code Lookup} object
     */
    @NotNull
    public static MethodHandles.Lookup newLookup(final @NotNull Class<?> lookupClass, final @Nullable Class<?> previousLookupClass, final int lookupModes) {
        // noinspection DataFlowIssue
        return Handles.invoke(LOOKUP_CONSTRUCTOR_MH, lookupClass, previousLookupClass, lookupModes);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Lookups() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(Lookups.class) + " cannot be instantiated");
    }

}
