package test;

import org.junit.jupiter.api.*;
import reflection.hacks.api.reflect.Classes;
import reflection.hacks.api.reflect.Reflection;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReflectionTest {

    @Test
    @Order(0)
    void testInit() throws Throwable {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        lookup.in(Reflection.class).ensureInitialized(Reflection.class);
        lookup.in(Classes.class).ensureInitialized(Classes.class);
    }

    @Test
    @SuppressWarnings("deprecation")
    void testGetAccessibleDeclaredField() throws Throwable {
        final String testGetAccessibleDeclaredField = "testGetAccessibleDeclaredField";
        final String valueFieldName = "value";

        final Field valueField = String.class.getDeclaredField(valueFieldName);
        Assertions.assertFalse(valueField.trySetAccessible());

        final Field accessibleValueField = Reflection.getAccessibleDeclaredField(String.class, valueFieldName);
        Assertions.assertTrue(accessibleValueField.isAccessible());

        final byte[] bytes = (byte[]) accessibleValueField.get(testGetAccessibleDeclaredField);

        final String newString = "newString";

        accessibleValueField.set(newString, bytes);

        Assertions.assertEquals(testGetAccessibleDeclaredField, newString);

    }

}
