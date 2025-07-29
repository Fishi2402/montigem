package umlp.pytest;

import org.graalvm.polyglot.Context;
import org.graalvm.python.embedding.utils.GraalPyResources;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class PyTest {
  private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase()
      .contains("windows");

  @Test
  public void testVenv() throws Exception {
    try (Context context = GraalPyResources.createContext()) {

      // Test some imports
      System.err.println("import websockets");
      context.eval("python", "import websockets;");

      System.err.println("import pydantic");
      context.eval("python", "import pydantic;");

      System.err.println("import asyncio");
      context.eval("python", "import asyncio;");
      System.err.println("import runtime");
      // load the runtime
      addToPath(context, new File("runtime-package/src"));
      System.err.println("import commandmanager");
      context.eval("python", "import montigem_runtime.commandmanager");

      addToPath(context, new File("build/py_out"));
      System.err.println("import MyDomain.A");
      context.eval("python", "import MyDomain.A");

      // TODO: Test generated
    }
  }


  protected void addToPath(Context context, File srcDir) throws FileNotFoundException {
    if (!srcDir.isDirectory()) {
      throw new FileNotFoundException(srcDir.getAbsolutePath());
    }
    String runtimeSrcNormalized = srcDir.getAbsolutePath().replace("\\", "/");

    context.eval("python", "import sys; sys.path.append(\"" + runtimeSrcNormalized + "\")");
  }

}
