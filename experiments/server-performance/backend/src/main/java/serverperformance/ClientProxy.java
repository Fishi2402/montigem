package serverperformance;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class ClientProxy implements AutoCloseable {
  private final URLClassLoader classLoader;
  private final Object clientInstance;
  private final Method execMethod;
  private final Method closeMethod;

  public ClientProxy(String wsAddress) {
    try {
      // Build URLs from the system classpath
      String cp = System.getProperty("java.class.path");
      String[] entries = cp.split(File.pathSeparator);
      URL[] urls = Arrays.stream(entries)
          .map(File::new)
          .map(file -> {
            try {
              return file.toURI().toURL();
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          })
          .toArray(URL[]::new);

      // Each client gets its own loader
      this.classLoader = new IsolatigClassLoader(
          urls,
          ClassLoader.getSystemClassLoader(),
          "serverperformance.", "umlp."
      );

      // Load and initialize client
      Class<?> clientClass = classLoader.loadClass("serverperformance.Client");
      this.clientInstance = clientClass
          .getConstructor(String.class)
          .newInstance(wsAddress);

      // Cache reflective methods
      this.execMethod = clientClass.getMethod("execRandomAction");
      this.closeMethod = clientClass.getMethod("close");
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize ClientProxy", e);
    }
  }

  /** Executes a random action on the client and returns roundtrip delay. */
  public int execRandomAction() {
    try {
      return (int) execMethod.invoke(clientInstance);
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute random action", e);
    }
  }

  @Override
  public void close() {
    try {
      closeMethod.invoke(clientInstance);
    } catch (Exception e) {
      throw new RuntimeException("Failed to close client", e);
    } finally {
      try {
        classLoader.close();
      } catch (Exception ignore) {
      }
    }
  }
}
