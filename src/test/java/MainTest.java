import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reflection.hacks.api.reflect.*;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainTest {
    @Test
    @SuppressWarnings({"deprecation", "unused"})
    void testMainFunctionality() {
        final Class<?> JavaLangAccess = Classes.load("jdk.internal.access.JavaLangAccess");
        Assertions.assertThrowsExactly(
                IllegalAccessException.class,
                () -> MethodHandles.lookup().in(JavaLangAccess).ensureInitialized(JavaLangAccess)
        );
        Assertions.assertThrowsExactly(
                IllegalAccessException.class,
                () -> MethodHandles.lookup().ensureInitialized(JavaLangAccess)
        );
        Classes.ensureInitialized(JavaLangAccess);

        Classes.ensureInitialized(sun.misc.Unsafe.class);

        final Field rootClassLoader = Fields.findDirectAccessible(Class.class, "classLoader");
        final Method rootGetUnsafe = Methods.findDirectAccessible(sun.misc.Unsafe.class, "getUnsafe");
        final Constructor<Void> rootVoidConstructor = Constructors.findAccessibleRoot(Void.class);

        Assertions.assertTrue(rootClassLoader.isAccessible());
        Assertions.assertNull(AccessibleObjects.getRoot(rootClassLoader));

        Assertions.assertTrue(rootGetUnsafe.isAccessible());
        Assertions.assertNull(AccessibleObjects.getRoot(rootGetUnsafe));

        Assertions.assertTrue(rootVoidConstructor.isAccessible());
        Assertions.assertNull(AccessibleObjects.getRoot(rootVoidConstructor));

        Assertions.assertThrowsExactly(
                NoSuchFieldException.class,
                () -> Class.class.getDeclaredField("classLoader")
        );

        Assertions.assertThrowsExactly(
                NoSuchMethodException.class,
                () -> sun.misc.Unsafe.class.getDeclaredMethod("getUnsafe")
        );

        final Field classLoader = Fields.unfilterAndFind(Class.class, "classLoader");
        final Method getUnsafe = Methods.unfilterAndFind(sun.misc.Unsafe.class, "getUnsafe");

        final Field classLoaderUnfiltered = Fields.find(Class.class, "classLoader");
        final Method getUnsafeUnfiltered = Methods.find(sun.misc.Unsafe.class, "getUnsafe");

        Assertions.assertNotNull(AccessibleObjects.getRoot(classLoaderUnfiltered));
        Assertions.assertNotNull(AccessibleObjects.getRoot(getUnsafeUnfiltered));

        final Constructor<Void> voidConstructor = Constructors.find(Void.class);

        Assertions.assertFalse(classLoader.trySetAccessible());
        Assertions.assertFalse(voidConstructor.trySetAccessible());

        AccessibleObjects.setAccessible(classLoader, voidConstructor);

        Assertions.assertTrue(classLoader.isAccessible());
        Assertions.assertTrue(voidConstructor.isAccessible());

    }

}
