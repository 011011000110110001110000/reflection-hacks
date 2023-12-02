package reflection.hacks.internal.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reflection.hacks.api.reflect.Classes;

import java.util.Objects;

/**
 * This class contains utility methods for dealing with {@link Throwable}s.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @since 1.0
 */
public final class Throwables {

    /**
     * Throws any given {@link Throwable} {@code t} without forcing the caller of this method to handle it even if {@code t} is a checked exception.
     *
     * @param t The {@link Throwable} to throw
     * @apiNote Passing {@code null} as the parameter to this method will result in a {@link NullPointerException} being thrown
     * @see Throwables#sneakyThrow(Throwable)
     */
    @SuppressWarnings("unused")
    public static void throwException(final @NotNull Throwable t) {
        throw Throwables.sneakyThrow(t);
    }

    /**
     * Wraps any {@link Throwable}s that are not {@link RuntimeException} into one.
     *
     * @param t The {@link Throwable} to wrap
     * @return a {@link RuntimeException} with {@code t} as the {@linkplain Throwable#cause cause}, or {@code t} itself if {@code t} is an instance of {@link RuntimeException}
     * @apiNote This method will wrap any {@link Error}s into a {@link RuntimeException}, despite {@link Error} being regarded as an unchecked exception
     * for the purposes of compile-time checking of exceptions. <br>
     * Passing {@code null} as the parameter to this method will result in a {@link NullPointerException} being thrown
     */
    @SuppressWarnings("unused")
    @NotNull
    public static RuntimeException wrapUnchecked(final @NotNull Throwable t) {
        Objects.requireNonNull(t, "throwable can't be null");

        if (t instanceof final RuntimeException re) {
            return re;
        }

        return new RuntimeException(t);
    }

    /**
     * Throws any given {@link Throwable} {@code t} without forcing the caller of this method to handle it even if {@code t} is a checked exception.
     * See section {@code 18.4} of <a href="https:/docs.oracle.com/javase/specs/jls/se17/html/jls-18.html#jls-18.4">chapter {@code 18} of the Java® Language Specification</a>
     * for details on how this works exactly.
     *
     * @param t   The {@link Throwable} to throw
     * @param <T> The type of the exception to throw, resolved to {@link RuntimeException} at compile time
     * @return T the given {@link Throwable} {@code t} (the value of {@code t} is never actually returned as {@code t} is immediately rethrown)
     * @throws T the given {@link Throwable} {@code t}
     * @apiNote This method will throw a {@link NullPointerException} if the supplied {@link Throwable} {@code t} is {@code null}
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T extends @NotNull Throwable> T sneakyThrow(final @NotNull Throwable t) throws T {
        throw (T) t;
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Throwables() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(Throwables.class) + " cannot be instantiated");
    }

}
