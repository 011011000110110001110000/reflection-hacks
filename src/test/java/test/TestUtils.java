package test;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class TestUtils {

    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public static boolean contains(Member[] members, String memberName) {
        for (Member member : members) {
            if (Objects.equals(member.getName(), memberName)) {
                return true;
            }
        }

        return false;
    }

}
