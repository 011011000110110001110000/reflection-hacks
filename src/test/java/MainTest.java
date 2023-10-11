import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reflection.hacks.api.reflect.AccessibleObjects;
import reflection.hacks.api.reflect.Constructors;
import reflection.hacks.api.reflect.Fields;
import reflection.hacks.api.reflect.Methods;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainTest {
    @Test
    @SuppressWarnings({"rawtypes", "deprecation"})
    void testMainFunctionality() {
        final Field classLoader = Fields.findDirectAccessible(Class.class, "classLoader");
        final Method getUnsafe = Methods.findDirectAccessible(sun.misc.Unsafe.class, "getUnsafe");
        final Constructor<Class> classConstructor = Constructors.findAccessibleRoot(Class.class);

        Assertions.assertTrue(classLoader.isAccessible());
        Assertions.assertNull(AccessibleObjects.getRoot(classLoader));

        Assertions.assertTrue(getUnsafe.isAccessible());
        Assertions.assertNull(AccessibleObjects.getRoot(getUnsafe));

        Assertions.assertTrue(classConstructor.isAccessible());
        Assertions.assertNull(AccessibleObjects.getRoot(classConstructor));
    }

}
