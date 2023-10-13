package reflection.hacks.internal.util.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reflection.hacks.internal.util.Throwables;

import java.util.Objects;

/**
 * Represents a generic portion of code that can return a value and throw {@link Throwable}s.
 * <p>
 * This is a {@linkplain java.util.function functional interface}
 * whose functional method is {@link ThrowingExecutable#execute()}.
 *
 * @param <T> the type of the returned value
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @since 1.0
 */
@FunctionalInterface
public interface ThrowingExecutable<T extends @Nullable Object> {

    /**
     * Executes a portion of code that may throw exceptions.
     *
     * @return the value returned by the code
     * @throws Throwable if any exception occurs
     */
    @Nullable T execute() throws Throwable;

    /**
     * Executes the given {@link ThrowingExecutable} {@code executable} and returns the non-null value produced by the execution,
     * re-throwing any {@link Throwable} {@code t} thrown by {@code executable} without forcing the caller of this method to handle it,
     * even if {@code t} is a checked exception.
     *
     * @param executable The {@link ThrowingExecutable} to execute
     * @param <T>        The type of the value returned by the {@code executable}
     * @return the value returned from the {@code executable}'s {@link ThrowingExecutable#execute() execute()} method
     * @throws NullPointerException if the value returned by {@code executable} is {@code null}
     */
    static <T> T execute(final @NotNull ThrowingExecutable<T> executable) {
        try {
            return Objects.requireNonNull(executable.execute(), "execution result cannot be null");
        } catch (Throwable t) {
            throw Throwables.sneakyThrow(t);
        }
    }

    /**
     * Executes the given {@link ThrowingExecutable} {@code executable} and returns the value produced by the execution,
     * re-throwing any {@link Throwable} {@code t} thrown by {@code executable} without forcing the caller of this method to handle it,
     * even if {@code t} is a checked exception.
     *
     * @param executable The {@link ThrowingExecutable} to execute
     * @param <T>        The type of the value returned by the {@code executable}
     * @return the value returned from the {@code executable}'s {@link ThrowingExecutable#execute() execute()} method
     */
    @Nullable
    static <T extends @Nullable Object> T executeNullable(final @NotNull ThrowingExecutable<T> executable) {
        try {
            return executable.execute();
        } catch (Throwable t) {
            throw Throwables.sneakyThrow(t);
        }
    }

}
