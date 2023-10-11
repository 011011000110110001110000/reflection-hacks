package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reflection.hacks.api.invoke.Handles;
import reflection.hacks.api.reflect.Classes;

import java.lang.invoke.VarHandle;

@SuppressWarnings({"unused", "FieldMayBeFinal", "FieldCanBeLocal"})
public class HandlesTest {

    @Test
    void testFindVarHandle() {

        Assertions.assertThrowsExactly(
                NoSuchFieldException.class,
                () -> Handles.findVarHandle(Inner.class, "nonExistent", Object.class)
        );

        final Inner testObj = new Inner();

        final VarHandle virtualPrimitiveFieldHandle = Handles.findVarHandle(Inner.class, "virtualPrimitiveField", int.class);
        final int virtualPrimitiveFieldValue = Classes.unchecked(virtualPrimitiveFieldHandle.get(testObj));
        Assertions.assertSame(testObj.virtualPrimitiveField, virtualPrimitiveFieldValue);

        final VarHandle virtualReferenceFieldHandle = Handles.findVarHandle(Inner.class, "virtualReferenceField", Object.class);
        final Object virtualReferenceFieldValue = Classes.unchecked(virtualReferenceFieldHandle.get(testObj));
        Assertions.assertSame(testObj.virtualReferenceField, virtualReferenceFieldValue);
    }

    @Test
    void testFindStaticVarHandle() {

        Assertions.assertThrowsExactly(
                NoSuchFieldException.class,
                () -> Handles.findStaticVarHandle(Inner.class, "nonExistent", Object.class)
        );

        final VarHandle staticPrimitiveFieldHandle = Handles.findStaticVarHandle(Inner.class, "staticPrimitiveField", int.class);
        final int staticPrimitiveFieldValue = Classes.unchecked(staticPrimitiveFieldHandle.get());
        Assertions.assertSame(Inner.staticPrimitiveField, staticPrimitiveFieldValue);

        final VarHandle staticReferenceFieldHandle = Handles.findStaticVarHandle(Inner.class, "staticReferenceField", Object.class);
        final Object staticReferenceFieldValue = Classes.unchecked(staticReferenceFieldHandle.get());
        Assertions.assertSame(Inner.staticReferenceField, staticReferenceFieldValue);
    }

    private static class Inner {
        private static int staticPrimitiveField;
        private static Object staticReferenceField;
        private int virtualPrimitiveField;
        private Object virtualReferenceField;

        static {
            staticPrimitiveField = 1;
            staticReferenceField = new Object();
        }

        Inner() {
            virtualPrimitiveField = 1;
            virtualReferenceField = new Object();
        }

    }

}
