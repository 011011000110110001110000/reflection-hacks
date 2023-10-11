module reflection.hacks.test {
    requires org.junit.jupiter.api;
    requires reflection.hacks.core;

    opens test to org.junit.platform.commons;
}