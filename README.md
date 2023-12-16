# reflection-hacks

This project leverages the JDK internals to effectively remove the limitations on reflection imposed by the module
system, introduced in Java 9.

### Why does this exist, considering there are already many other libraries that do the same thing?

This all started before I knew about those projects, and I was messing around with attaching Java Agent dynamically to
running Java processes.
I didn't actually need to worry about modules for what I was doing (and the Instrumentation API already provides a
supported API for getting around module restrictions anyway),
but the more I looked into the inner workings of the JDK, the more I started wondering if it was possible to bypass the
various access restrictions and bring reflection
back to its former glory (seriously, Java 8 reflection is overpowered!).
And eventually, I ended up with something that I think is worth sharing, even if it's "just another reflection library".

### How does this work?

If you are familiar with this kind of project, you're probably thinking something along the lines of "I bet this just
uses sun.misc.Unsafe, like all the other libraries do".
After all, there is no other way to achieve what this project claims to be able to do, right?

But, as you might have now guessed, this project does __not__ rely on sun.misc.Unsafe!

How this actually works is by abusing a quirk in how Proxy classes implementing interfaces in non-exported packages are
assigned their module membership.
Combining the aforementioned quirk with a user controlled ClassLoader and a good amount of JDK internals (namely the
jdk.internal.access package), we get ~~a recipe for disaster~~ exactly what we need!
For the exact details, look into the reflection.hacks.internal package, everything in there is documented despite
technically being intended for internal usage only.

### Is this vendor specific?

Given it relies on implementation details of the JDK, it most likely is, although I have yet to find a vendor for which
it doesn't work.
All vendors for which this has been tested can be found in the build.gradle script.

### Should I use this?

Most likely not, since Java provides supported APIs for doing (almost) everything this project can do, be it via a Java
Agent, with specific command-line arguments or even by just having client code open its packages to your module.
However, if you find yourself in a situation where you need to use reflection and the options described above are not
available, then this library might just be what you need. Moreover, if for some reason you don't have
access to sun.misc.Unsafe, as already mentioned this library will still work, unlike all the others that I've seen.