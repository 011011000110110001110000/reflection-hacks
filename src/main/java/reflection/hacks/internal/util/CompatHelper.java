package reflection.hacks.internal.util;

import org.jetbrains.annotations.NotNull;
import reflection.hacks.api.invoke.Handles;
import reflection.hacks.api.reflect.Classes;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * This class contains utility methods for obtaining handles to internal methods of different JDK implementations. <br>
 * More specifically, this class handles compatibility with the <a href=https://projects.eclipse.org/projects/technology.openj9>OpenJ9™</a> VM.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @version 1.0
 * @since 1.0
 */
public class CompatHelper {

    /**
     * Cached {@link VarHandle} for the cached reflection data of a {@link Class}
     */
    @NotNull
    private static final VarHandle REFLECTION_CACHE;

    /**
     * Cached {@link MethodHandle} for the native method that retrieves the declared {@link Field}s of a {@link Class}
     */
    @NotNull
    private static final MethodHandle NATIVE_GET_DECLARED_FIELDS_MH;

    /**
     * Cached {@link MethodHandle} for the native method that retrieves the declared {@link Method}s of a {@link Class}
     */
    @NotNull
    private static final MethodHandle NATIVE_GET_DECLARED_METHODS_MH;

    static {

        final String hotSpotClassName = "java.lang.Class$ReflectionData";
        final String hotSpotFieldName = "reflectionData";
        final String openJ9ClassName = "java.lang.Class$ReflectCache";
        final String openJ9FieldName = "reflectCache";

        final Class<?> reflectionCacheClass;
        final String reflectionCacheFieldName;

        final MethodHandle nativeGetDeclaredFields_MH;
        final MethodHandle nativeGetDeclaredMethods_MH;

        boolean hotSpot = true;

        try {
            Class.forName(hotSpotClassName);
        } catch (ClassNotFoundException cnfe) {
            hotSpot = false;
        }

        try {
            if (hotSpot) {
                reflectionCacheClass = SoftReference.class;
                reflectionCacheFieldName = hotSpotFieldName;

                nativeGetDeclaredFields_MH = MethodHandles.insertArguments(Handles.findVirtual(Class.class, "getDeclaredFields0", Field[].class, boolean.class), 1, false);
                nativeGetDeclaredMethods_MH = MethodHandles.insertArguments(Handles.findVirtual(Class.class, "getDeclaredMethods0", Method[].class, boolean.class), 1, false);
            } else {
                reflectionCacheClass = Class.forName(openJ9ClassName);
                reflectionCacheFieldName = openJ9FieldName;

                nativeGetDeclaredFields_MH = Handles.findVirtual(Class.class, "getDeclaredFieldsImpl", Field[].class);
                nativeGetDeclaredMethods_MH = Handles.findVirtual(Class.class, "getDeclaredMethodsImpl", Method[].class);
            }
        } catch (final Throwable t) {
            throw Throwables.sneakyThrow(t);
        }

        REFLECTION_CACHE = Handles.findVarHandle(Class.class, reflectionCacheFieldName, reflectionCacheClass);

        NATIVE_GET_DECLARED_FIELDS_MH = nativeGetDeclaredFields_MH;
        NATIVE_GET_DECLARED_METHODS_MH = nativeGetDeclaredMethods_MH;

    }

    /**
     * Standard getter for {@link CompatHelper#REFLECTION_CACHE}.
     *
     * @return a {@link VarHandle} for the cached reflection data of a {@link Class}
     */
    @NotNull
    public static VarHandle reflectionCacheHandle() {
        return CompatHelper.REFLECTION_CACHE;
    }

    /**
     * Standard getter for {@link CompatHelper#NATIVE_GET_DECLARED_FIELDS_MH}.
     *
     * @return a {@link MethodHandle} for the native method that retrieves the declared {@link Field}s of a {@link Class}
     */
    @NotNull
    public static MethodHandle nativeDeclaredFieldRetriever() {
        return CompatHelper.NATIVE_GET_DECLARED_FIELDS_MH;
    }

    /**
     * Standard getter for {@link CompatHelper#NATIVE_GET_DECLARED_METHODS_MH}.
     *
     * @return a {@link MethodHandle} for the native method that retrieves the declared {@link Method}s of a {@link Class}
     */
    @NotNull
    public static MethodHandle nativeDeclaredMethodRetriever() {
        return CompatHelper.NATIVE_GET_DECLARED_METHODS_MH;
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private CompatHelper() {
        throw new UnsupportedOperationException(Classes.moduleInclusiveName(CompatHelper.class) + " cannot be instantiated");
    }

}
