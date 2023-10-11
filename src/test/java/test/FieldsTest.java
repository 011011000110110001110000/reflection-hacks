package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reflection.hacks.api.reflect.Fields;

import java.lang.reflect.Field;
import java.util.Arrays;

@SuppressWarnings("SameParameterValue")
public class FieldsTest {

    @Test
    void testGetDirect() throws Throwable {
        final Inner testObj = new Inner();
        final Object staticFieldValue = Inner.staticField;
        final Object virtualFieldValue = testObj.virtualField;

        final Field[] innerFields = Inner.class.getDeclaredFields();
        final Field[] directInnerFields = Fields.getDirect(Inner.class);

        Assertions.assertArrayEquals(innerFields, directInnerFields);

        final int staticFieldIndex = TestUtils.isStatic(directInnerFields[0]) ? 0 : 1;

        final Field directStaticField = directInnerFields[staticFieldIndex];
        final Field directVirtualField = directInnerFields[1 - staticFieldIndex];

        Assertions.assertEquals(staticFieldValue, directStaticField.get(null));
        Assertions.assertEquals(virtualFieldValue, directVirtualField.get(testObj));

        final Field[] classFields = Class.class.getDeclaredFields();
        final Field[] directClassFields = Fields.getDirect(Class.class);

        // Class#classLoader is filtered from regular reflective access
        final String fieldName = "classLoader";

        Assertions.assertFalse(Arrays.equals(classFields, directClassFields));

        Assertions.assertFalse(TestUtils.contains(classFields, fieldName));
        Assertions.assertTrue(TestUtils.contains(directClassFields, fieldName));
    }

    @SuppressWarnings("unused")
    private static class Inner {
        private static final Object staticField = new Object();
        private final Object virtualField = new Object();
    }

}
