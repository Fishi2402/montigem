package serverperformance;

import java.net.URL;
import java.net.URLClassLoader;

public class IsolatigClassLoader extends URLClassLoader {
  private final String[] childFirstPackages;

  public IsolatigClassLoader(URL[] urls, ClassLoader parent, String... childFirstPackagesPrefixes) {
    super(urls, parent);
    this.childFirstPackages = childFirstPackagesPrefixes;
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // For packages we want isolated, try loading them ourselves first
    for (String prefix : childFirstPackages) {
      if (name.startsWith(prefix)) {
        synchronized (getClassLoadingLock(name)) {
          Class<?> loaded = findLoadedClass(name);
          if (loaded == null) {
            try {
              loaded = findClass(name); // load from this classloader
            } catch (ClassNotFoundException e) {
              // fall back to parent if not found
            }
          }
          if (loaded != null) {
            if (resolve) resolveClass(loaded);
            return loaded;
          }
        }
      }
    }
    // Default: delegate to parent
    return super.loadClass(name, resolve);
  }
}
