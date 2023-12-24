/**
 * This module contains the core API of the reflection-hacks library.
 *
 * @author <a href=https://github.com/011011000110110001110000>011011000110110001110000</a>
 * @since 1.0
 */
module reflection.hacks.core {
    requires static org.jetbrains.annotations;
    requires org.objectweb.asm;

    exports reflection.hacks.api.invoke;
    exports reflection.hacks.api.reflect;
}