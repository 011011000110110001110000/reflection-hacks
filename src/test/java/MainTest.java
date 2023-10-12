import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reflection.hacks.api.reflect.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainTest {
    @Test
    @SuppressWarnings({"rawtypes", "deprecation", "unused"})
    void testMainFunctionality() {
        Classes.ensureInitialized(sun.misc.Unsafe.class);

        final Field rootClassLoader = Fields.findDirectAccessible(Class.class, "classLoader");
        final Method rootGetUnsafe = Methods.findDirectAccessible(sun.misc.Unsafe.class, "getUnsafe");
        final Constructor<Class> rootClassConstructor = Constructors.findAccessibleRoot(Class.class);

        Assertions.assertTrue(rootClassLoader.isAccessible());
        Assertions.assertNull(AccessibleObjects.getRoot(rootClassLoader));

        Assertions.assertTrue(rootGetUnsafe.isAccessible());
        Assertions.assertNull(AccessibleObjects.getRoot(rootGetUnsafe));

        Assertions.assertTrue(rootClassConstructor.isAccessible());
        Assertions.assertNull(AccessibleObjects.getRoot(rootClassConstructor));

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

        final Constructor<Class> classConstructor = Constructors.find(Class.class);

        Assertions.assertFalse(classLoader.trySetAccessible());
        Assertions.assertFalse(classConstructor.trySetAccessible());

        AccessibleObjects.setAccessible(classLoader, classConstructor);

        Assertions.assertTrue(classLoader.isAccessible());
        Assertions.assertTrue(classConstructor.isAccessible());

    }

}
