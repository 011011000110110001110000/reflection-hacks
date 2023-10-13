package reflection.hacks.internal.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A thread-safe holder for a constant value of type {@code T} that will only be initialized
 * the first time the value is needed (see {@link Lazy#get()}).
 *
 * @param <T> The type of the value held by the {@link Lazy} instance
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @since 1.0
 */
public class Lazy<T> implements Supplier<T> {

    /**
     * Cached {@link Supplier} instance that throws an exception when asked for its value
     */
    @NotNull
    @SuppressWarnings("rawtypes")
    private static final Supplier NO_SUPPLIER;

    /**
     * Default value for uninitialized {@link Lazy} instances
     */
    @NotNull
    private static final Object NO_VALUE;

    static {

        NO_SUPPLIER = () -> {
            throw new UnsupportedOperationException();
        };

        NO_VALUE = new Object();

    }

    /**
     * The value held by this {@link Lazy} instance
     */
    private volatile Object value;

    /**
     * The {@link Supplier} used to initialize {@link Lazy#value} when needed
     */
    @NotNull
    private Supplier<T> valueSupplier;

    /**
     * Used for synchronization
     */
    @NotNull
    private final Object lock;

    /**
     * Private to prevent direct instantiation by outside code.
     *
     * @param value         The initial value held by this {@link Lazy} instance
     * @param valueSupplier The {@link Supplier} that will provide the value
     */
    private Lazy(final Object value, final @NotNull Supplier<T> valueSupplier) {
        this.value = value;
        this.valueSupplier = valueSupplier;

        this.lock = new Object();
    }

    /**
     * Private to prevent direct instantiation by outside code.
     * This constructor produces an instance which is not initialized.
     *
     * @param valueSupplier The {@link Supplier} that will provide the value
     * @see Lazy#isInitialized()
     */
    private Lazy(final @NotNull Supplier<T> valueSupplier) {
        this(Lazy.NO_VALUE, valueSupplier);
    }

    /**
     * Private to prevent direct instantiation by outside code.
     * This constructor produces an instance which is already initialized
     * to {@code value}.
     *
     * @param value The value held by the created instance
     * @see Lazy#isInitialized()
     */
    @SuppressWarnings("unchecked")
    private Lazy(final T value) {
        // Avoid allocating a new Supplier<T> each time
        // since it will never be used anyway
        this(value, Lazy.NO_SUPPLIER);
    }

    /**
     * Returns the value held by this instance, initializing it
     * if it hasn't already been.
     *
     * @return the value held by this instance
     * @see Lazy#isInitialized()
     */
    @SuppressWarnings("unchecked")
    public T get() {
        if (this.value == Lazy.NO_VALUE) {
            synchronized (this.lock) {
                if (this.value == Lazy.NO_VALUE) {
                    this.value = valueSupplier.get();
                    // Make the supplier eligible for garbage collection
                    this.valueSupplier = Lazy.NO_SUPPLIER;
                }
            }
        }

        return (T) this.value;
    }

    /**
     * Checks whether the value held by this instance
     * has been initialized.
     *
     * @return {@code true} if the value has been initialized, {@code false} otherwise
     */
    @Contract(pure = true)
    public boolean isInitialized() {
        return this.value != Lazy.NO_VALUE;
    }

    @Override
    public String toString() {
        return "Lazy{" +
                "value=" + this.value +
                ", valueSupplier=" + this.valueSupplier +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Lazy<?> lazy = (Lazy<?>) o;
        return Objects.equals(this.value, lazy.value) && Objects.equals(this.valueSupplier, lazy.valueSupplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.valueSupplier);
    }

    /**
     * Constructs a new, {@linkplain Lazy#isInitialized() uninitialized} instance of {@link Lazy}
     * that will use the given {@link Supplier valueSupplier} to initialize its held value.
     *
     * @param valueSupplier The {@link Supplier} that will be used to initialize the {@link Lazy#value value}
     *                      held by the returned instance
     * @param <T>           The type of the value held by the returned instance
     * @return the newly created instance
     */
    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static <T extends @Nullable Object> Lazy<T> of(final @NotNull Supplier<T> valueSupplier) {
        return new Lazy<>(valueSupplier);
    }

    /**
     * Constructs a new, {@linkplain Lazy#isInitialized() already initialized} instance of {@link Lazy}
     * that holds the given {@code value}.
     *
     * @param value The value held by the returned {@link Lazy} instance
     * @param <T>   The type of the value held by the returned instance
     * @return the newly created instance
     */
    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static <T extends @Nullable Object> Lazy<T> of(final @Nullable T value) {
        return new Lazy<>(value);
    }

}
