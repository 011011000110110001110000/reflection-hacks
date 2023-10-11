package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reflection.hacks.api.reflect.Methods;

public class MethodsTest {

    @Test
    void testFindDirect() {
        Assertions.assertThrowsExactly(
                NoSuchMethodException.class,
                () -> Methods.findDirect(Inner.class, "nonExistent", Object.class)
        );
    }

    private static final class Inner {

    }

}
