package reflection.hacks.api.invoke;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reflection.hacks.api.reflect.Classes;
import reflection.hacks.internal.util.function.ThrowingExecutable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * This class provides an API for obtaining and working with {@link MethodHandle} and {@link VarHandle} instances.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @apiNote All {@link MethodHandle}s produced by members of this class are not bound to any specific caller.
 * @since 1.0
 */
public final class Handles {

    /**
     * Produces a method handle for a virtual method. <br>
     * See the documentation for {@link MethodHandles.Lookup#findVirtual(Class, String, MethodType)} for details.
     *
     * @param owner          The class or interface from which the method is accessed
     * @param name           The name of the method
     * @param returnType     The return type of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the desired method handle
     * @see Handles#findVirtual(Class, String, MethodType)
     */
    @NotNull
    public static MethodHandle findVirtual(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull Class<?> returnType, final @NotNull Class<?>... parameterTypes) {
        return Handles.findVirtual(owner, name, MethodType.methodType(returnType, parameterTypes));
    }

    /**
     * Produces a method handle for a virtual method. <br>
     * See the documentation for {@link MethodHandles.Lookup#findVirtual(Class, String, MethodType)} for details.
     *
     * @param owner The class or interface from which the method is accessed
     * @param name  The name of the method
     * @param type  The type of the method, with the receiver argument omitted
     * @return the desired method handle
     */
    @NotNull
    public static MethodHandle findVirtual(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull MethodType type) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.findVirtual(owner, name, type)
        );
    }

    /**
     * Produces a method handle for a virtual method, bound to the given {@code instance}. <br>
     * See the documentation for {@link MethodHandles.Lookup#findVirtual(Class, String, MethodType)} for details.
     *
     * @param owner          The class or interface from which the method is accessed
     * @param instance       The instance to which the method handle is bound
     * @param name           The name of the method
     * @param returnType     The return type of the method
     * @param parameterTypes The parameter types of the method, in order
     * @param <O>            The type of the class from which the method is accessed
     * @param <I>            The type of the {@code instance} the method handle will be bound to
     * @return the desired method handle
     * @see Handles#findVirtualAndBind(Class, Object, String, MethodType)
     */
    @NotNull
    public static <O, I extends O> MethodHandle findVirtualAndBind(final @NotNull Class<O> owner, final @Nullable I instance, final @NotNull String name, final @NotNull Class<?> returnType, final @NotNull Class<?>... parameterTypes) {
        return Handles.findVirtualAndBind(owner, instance, name, MethodType.methodType(returnType, parameterTypes));
    }

    /**
     * Produces a method handle for a virtual method, bound to the given {@code instance}. <br>
     * See the documentation for {@link MethodHandles.Lookup#findVirtual(Class, String, MethodType)} for details.
     *
     * @param owner    The class or interface from which the method is accessed
     * @param instance The instance to which the method handle is bound
     * @param name     The name of the method
     * @param type     The type of the method, with the receiver argument omitted
     * @param <O>      The type of the class from which the method is accessed
     * @param <I>      The type of the {@code instance} the method handle will be bound to
     * @return the desired method handle
     * @see Handles#bind(MethodHandle, Object)
     */
    @NotNull
    public static <O, I extends O> MethodHandle findVirtualAndBind(final @NotNull Class<O> owner, final @Nullable I instance, final @NotNull String name, final @NotNull MethodType type) {
        return Handles.bind(Handles.findVirtual(owner, name, type), instance);
    }

    /**
     * Produces a method handle for a static method. <br>
     * See the documentation for {@link MethodHandles.Lookup#findStatic(Class, String, MethodType)} for details.
     *
     * @param owner          The class from which the method is accessed
     * @param name           The name of the method
     * @param returnType     The return type of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the desired method handle
     */
    @NotNull
    public static MethodHandle findStatic(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull Class<?> returnType, final @NotNull Class<?>... parameterTypes) {
        return Handles.findStatic(owner, name, MethodType.methodType(returnType, parameterTypes));
    }

    /**
     * Produces a method handle for a static method. <br>
     * See the documentation for {@link MethodHandles.Lookup#findStatic(Class, String, MethodType)} for details.
     *
     * @param owner The class from which the method is accessed
     * @param name  The name of the method
     * @param type  The type of the method
     * @return the desired method handle
     */
    @NotNull
    public static MethodHandle findStatic(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull MethodType type) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.findStatic(owner, name, type)
        );
    }

    /**
     * Produces an early-bound method handle for a virtual method. <br>
     * It will bypass checks for overriding methods on the receiver, as if called from an {@code invokespecial} instruction
     * from within the explicitly specified {@code specialCaller}. <br>
     * See the documentation for {@link MethodHandles.Lookup#findSpecial(Class, String, MethodType, Class)} for details.
     *
     * @param owner          The class or interface from which the method is accessed
     * @param name           The name of the method (which must not be "&lt;init&gt;")
     * @param specialCaller  The proposed calling class to perform the {@code invokespecial}
     * @param returnType     The return type of the method
     * @param parameterTypes The parameter types of the method, in order
     * @return the desired method handle
     * @see Handles#findSpecial(Class, String, Class, MethodType)
     */
    @NotNull
    public static MethodHandle findSpecial(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull Class<?> specialCaller, final @NotNull Class<?> returnType, final @NotNull Class<?>... parameterTypes) {
        return Handles.findSpecial(owner, name, specialCaller, MethodType.methodType(returnType, parameterTypes));
    }

    /**
     * Produces an early-bound method handle for a virtual method. <br>
     * It will bypass checks for overriding methods on the receiver, as if called from an {@code invokespecial} instruction
     * from within the explicitly specified {@code specialCaller}. <br>
     * See the documentation for {@link MethodHandles.Lookup#findSpecial(Class, String, MethodType, Class)} for details.
     *
     * @param owner         The class or interface from which the method is accessed
     * @param name          The name of the method (which must not be "&lt;init&gt;")
     * @param specialCaller The proposed calling class to perform the {@code invokespecial}
     * @param type          The type of the method, with the receiver argument omitted
     * @return the desired method handle
     */
    @NotNull
    public static MethodHandle findSpecial(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull Class<?> specialCaller, final @NotNull MethodType type) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.findSpecial(owner, name, type, specialCaller)
        );
    }

    /**
     * Produces a method handle which creates an object and initializes it, using the constructor of the specified type. <br>
     * See the documentation for {@link MethodHandles.Lookup#findConstructor(Class, MethodType)} for details.
     *
     * @param owner          The class or interface from which the method is accessed
     * @param parameterTypes The parameter types of the constructor, in order
     * @return the desired method handle
     * @see Handles#findConstructor(Class, MethodType)
     */
    @NotNull
    public static MethodHandle findConstructor(final @NotNull Class<?> owner, final Class<?>... parameterTypes) {
        return Handles.findConstructor(owner, MethodType.methodType(void.class, parameterTypes));
    }

    /**
     * Produces a method handle which creates an object and initializes it, using the constructor of the specified type. <br>
     * See the documentation for {@link MethodHandles.Lookup#findConstructor(Class, MethodType)} for details.
     *
     * @param owner The class or interface from which the method is accessed
     * @param type  The type of the method, with the receiver argument omitted, and a void return type
     * @return the desired method handle
     */
    @NotNull
    public static MethodHandle findConstructor(final @NotNull Class<?> owner, final @NotNull MethodType type) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.findConstructor(owner, type)
        );
    }

    /**
     * Produces a method handle giving read access to a non-static field. <br>
     * See the documentation for {@link MethodHandles.Lookup#findGetter(Class, String, Class)} for details.
     *
     * @param owner The class or interface from which the method is accessed
     * @param name  The field's name
     * @param type  The field's type
     * @return a method handle which can load values from the field
     */
    @NotNull
    public static MethodHandle findGetter(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull Class<?> type) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.findGetter(owner, name, type)
        );
    }

    /**
     * Produces a method handle giving read access to a non-static field, bound to the given {@code instance}. <br>
     * See the documentation for {@link MethodHandles.Lookup#findGetter(Class, String, Class)} for details.
     *
     * @param owner    The class or interface from which the method is accessed
     * @param instance The instance to which the method handle is bound
     * @param name     The field's name
     * @param type     The field's type
     * @param <O>      The type of the class from which the field is accessed
     * @param <I>      The type of the {@code instance} the method handle will be bound to
     * @return a method handle which can load values from the field for {@code instance}
     * @see Handles#bind(MethodHandle, Object)
     */
    @NotNull
    public static <O, I extends O> MethodHandle findGetterAndBind(final @NotNull Class<O> owner, final @NotNull I instance, final @NotNull String name, final @NotNull Class<?> type) {
        return Handles.bind(Handles.findGetter(owner, name, type), instance);
    }

    /**
     * Produces a method handle giving read access to a static field. <br>
     * See the documentation for {@link MethodHandles.Lookup#findStaticGetter(Class, String, Class)} for details.
     *
     * @param owner The class or interface from which the method is accessed
     * @param name  The field's name
     * @param type  The field's type
     * @return a method handle which can load values from the field
     */
    @NotNull
    public static MethodHandle findStaticGetter(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull Class<?> type) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.findStaticGetter(owner, name, type)
        );
    }

    /**
     * Produces a method handle giving write access to a non-static field. <br>
     * See the documentation for {@link MethodHandles.Lookup#findSetter(Class, String, Class)} for details.
     *
     * @param owner The class or interface from which the method is accessed
     * @param name  The field's name
     * @param type  The field's type
     * @return a method handle which can store values into the field
     */
    @NotNull
    public static MethodHandle findSetter(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull Class<?> type) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.findSetter(owner, name, type)
        );
    }

    /**
     * Produces a method handle giving write access to a non-static field, bound to the given {@code instance}. <br>
     * See the documentation for {@link MethodHandles.Lookup#findSetter(Class, String, Class)} for details.
     *
     * @param owner    The class or interface from which the method is accessed
     * @param instance The instance to which the method handle is bound
     * @param name     The field's name
     * @param type     The field's type
     * @param <O>      The type of the class from which the field is accessed
     * @param <I>      The type of the {@code instance} the method handle will be bound to
     * @return a method handle which can store values into the field for {@code instance}
     * @see Handles#bind(MethodHandle, Object)
     */
    @NotNull
    public static <O, I extends O> MethodHandle findSetterAndBind(final @NotNull Class<O> owner, final @NotNull I instance, final @NotNull String name, final @NotNull Class<?> type) {
        return Handles.bind(Handles.findSetter(owner, name, type), instance);
    }

    /**
     * Produces a method handle giving write access to a static field. <br>
     * See the documentation for {@link MethodHandles.Lookup#findStaticSetter(Class, String, Class)} for details.
     *
     * @param owner The class or interface from which the method is accessed
     * @param name  The field's name
     * @param type  The field's type
     * @return a method handle which can load values from the field
     */
    @NotNull
    public static MethodHandle findStaticSetter(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull Class<?> type) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.findStaticSetter(owner, name, type)
        );
    }

    /**
     * Produces a VarHandle giving access to a non-static field {@code name} of
     * type {@code type} declared in a class of type {@code owner}. <br>
     * See the documentation for {@link MethodHandles.Lookup#findVarHandle(Class, String, Class)} for details.
     *
     * @param owner The class that declares the field
     * @param name  The field's name
     * @param type  The field's type
     * @return a VarHandle giving access to non-static fields
     */
    @NotNull
    public static VarHandle findVarHandle(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull Class<?> type) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.findVarHandle(owner, name, type)
        );
    }

    /**
     * Produces a VarHandle giving access to a static field {@code name} of
     * type {@code type} declared in a class of type {@code owner}. <br>
     * See the documentation for {@link MethodHandles.Lookup#findStaticVarHandle(Class, String, Class)} for details.
     *
     * @param owner The class that declares the static field
     * @param name  The field's name
     * @param type  The field's type
     * @return a VarHandle giving access to a static field
     */
    @NotNull
    public static VarHandle findStaticVarHandle(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull Class<?> type) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.findStaticVarHandle(owner, name, type)
        );
    }

    /**
     * Makes a direct method handle to m. <br>
     * See the documentation for {@link MethodHandles.Lookup#unreflect(Method)} for details.
     *
     * @param m The reflected method
     * @return a method handle which can invoke the reflected method
     */
    @NotNull
    public static MethodHandle unreflect(final @NotNull Method m) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.unreflect(m)
        );
    }

    /**
     * Produces a method handle for a reflected method. <br>
     * It will bypass checks for overriding methods on the receiver, as if called
     * from an {@code invokespecial} instruction from within the explicitly specified specialCaller. <br>
     * See the documentation for {@link MethodHandles.Lookup#unreflectSpecial(Method, Class)} for details.
     *
     * @param m             The reflected method
     * @param specialCaller The class nominally calling the method
     * @return a method handle which can invoke the reflected method
     */
    @NotNull
    public static MethodHandle unreflectSpecial(final @NotNull Method m, final @NotNull Class<?> specialCaller) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.unreflectSpecial(m, specialCaller)
        );
    }

    /**
     * Produces a method handle for a reflected constructor. <br>
     * See the documentation for {@link MethodHandles.Lookup#unreflectConstructor(Constructor)} for details.
     *
     * @param c The reflected constructor
     * @return a method handle which can invoke the reflected constructor
     */
    @NotNull
    public static MethodHandle unreflectConstructor(final @NotNull Constructor<?> c) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.unreflectConstructor(c)
        );
    }

    /**
     * Produces a method handle giving read access to a reflected field. <br>
     * See the documentation for {@link MethodHandles.Lookup#unreflectGetter(Field)} for details.
     *
     * @param f The reflected field
     * @return a method handle which can load values from the reflected field
     */
    @NotNull
    public static MethodHandle unreflectGetter(final @NotNull Field f) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.unreflectGetter(f)
        );
    }

    /**
     * Produces a method handle giving write access to a reflected field. <br>
     * See the documentation for {@link MethodHandles.Lookup#unreflectSetter(Field)} for details.
     *
     * @param f The reflected field
     * @return a method handle which can store values into the reflected field
     */
    @NotNull
    public static MethodHandle unreflectSetter(final @NotNull Field f) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.unreflectSetter(f)
        );
    }

    /**
     * Produces a VarHandle giving access to a reflected field {@code f}. <br>
     * See the documentation for {@link MethodHandles.Lookup#unreflectVarHandle(Field)} for details.
     *
     * @param f The reflected field
     * @return a {@link VarHandle} giving access to non-static fields or a static field
     */
    @NotNull
    public static VarHandle unreflectVarHandle(final @NotNull Field f) {
        return ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.unreflectVarHandle(f)
        );
    }

    /**
     * Invokes the non-static method with the given name, parameter types and return type on the given object.
     *
     * @param instance      The object to invoke the method on
     * @param name          The name of the method
     * @param returnType    The return type of the method
     * @param argumentTypes The argument types of the method
     * @param arguments     The arguments to use when invoking the method
     * @param <T>           The return type of the method
     * @return the value returned by the method, cast to the appropriate type
     * @see #invokeNonStatic(Object, String, MethodType, Object...)
     */
    @Nullable
    public static <T> T invokeNonStatic(final @NotNull Object instance, final @NotNull String name, final @NotNull Class<T> returnType, final @NotNull Class<?>[] argumentTypes, final @Nullable Object... arguments) {
        return Classes.unchecked(Handles.invokeNonStatic(instance, name, MethodType.methodType(returnType, argumentTypes), arguments));
    }

    /**
     * Invokes the non-static method with the given name, parameter types and return type on the given object.
     *
     * @param instance  The object to invoke the method on
     * @param name      The name of the method
     * @param type      The type of the method
     * @param arguments The arguments to use for the method invocation
     * @return the value returned by the method, as an {@link Object}
     */
    public static Object invokeNonStatic(final @NotNull Object instance, final @NotNull String name, final @NotNull MethodType type, final @Nullable Object... arguments) {
        final MethodHandle handle = ThrowingExecutable.execute(
                () -> Lookups.LOOKUP.bind(instance, name, type)
        );

        // Invoke the handle outside the lambda for clarity
        return Handles.invoke(handle, arguments);
    }

    /**
     * Invokes the static method with the given name, parameter types and return type declared by class {@code owner}.
     *
     * @param owner         The class that declares the method
     * @param name          The name of the method
     * @param returnType    The return type of the method
     * @param argumentTypes The argument types of the method
     * @param arguments     The arguments to use when invoking the method
     * @param <T>           The return type of the method
     * @return the value returned by the method, cast to the appropriate type
     * @see #invokeStatic(Class, String, MethodType, Object...)
     */
    @Nullable
    public static <T> T invokeStatic(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull Class<T> returnType, final @NotNull Class<?>[] argumentTypes, final @Nullable Object... arguments) {
        return Classes.unchecked(Handles.invokeStatic(owner, name, MethodType.methodType(returnType, argumentTypes), arguments));
    }

    /**
     * Invokes the static method with the given name, parameter types and return type declared by class {@code owner}.
     *
     * @param owner     The class that declares the method
     * @param name      The name of the method
     * @param type      The type of the method
     * @param arguments The arguments to use for the method invocation
     * @return the value returned by the method, as an {@link Object}
     */
    public static Object invokeStatic(final @NotNull Class<?> owner, final @NotNull String name, final @NotNull MethodType type, final @Nullable Object... arguments) {
        final MethodHandle handle = ThrowingExecutable.execute(
                () -> Handles.findStatic(owner, name, type)
        );

        // Invoke the handle outside the lambda for clarity
        return Handles.invoke(handle, arguments);
    }

    /**
     * Invokes a {@link MethodHandle} without forcing the caller of this method
     * to handle any {@link Throwable} {@code t} thrown by the invocation.
     *
     * @param handle The method handle to invoke
     * @param args   The arguments passed to the handle's {@link MethodHandle#invokeWithArguments(Object...) invoke} method
     * @param <T>    The return type of the method
     * @return the value returned from the invocation of the given {@code handle}, cast to the appropriate type {@code T}
     */
    public static <T> T invoke(final @NotNull MethodHandle handle, final @Nullable Object... args) {
        return Classes.unchecked(
                ThrowingExecutable.execute(
                        () -> handle.invokeWithArguments(args)
                )
        );
    }

    /**
     * Binds a value {@code val} to the first argument of a method handle, without invoking it. <br>
     * See the documentation for {@link MethodHandle#bindTo(Object)} for details.
     *
     * @param handle The {@link MethodHandle} to bind the value to
     * @param val    The value to bind to {@code handle}
     * @return a new method handle which prepends the given value to the incoming argument list, before calling the original method handle
     */
    @NotNull
    @Contract("_, _ -> new")
    public static MethodHandle bind(final @NotNull MethodHandle handle, final @Nullable Object val) {
        return ThrowingExecutable.execute(
                () -> handle.bindTo(val)
        );
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Handles() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(Handles.class) + " cannot be instantiated");
    }

}
