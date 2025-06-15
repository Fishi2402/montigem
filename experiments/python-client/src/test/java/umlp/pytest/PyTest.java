package umlp.pytest;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class PyTest {
  @Test
  public void testPy() throws FileNotFoundException {
    File runtimeSrc = new File("runtime-package/src");
    if (!runtimeSrc.isDirectory()) {
      throw new FileNotFoundException(runtimeSrc.getAbsolutePath());
    }
    String runtimeSrcNormalized = runtimeSrc.getAbsolutePath().replace("\\", "/");
//    File pipDir = new File("build/py_pip/");
//    if (!pipDir.isDirectory()) {
//      throw new FileNotFoundException(pipDir.getAbsolutePath());
//    }

    try (Context context = Context.newBuilder("python").allowAllAccess(true).build()) {
      // Add the montigem_runtime dependency to the path
      context.eval("python", "import sys");
      context.eval("python", "sys.path.append(\"" + runtimeSrcNormalized + "\")");
      // Also import pydantic
//      for (File file : pipDir.listFiles()) {
//        if (!file.isDirectory()) continue;
//        if (file.getName().startsWith("pydantic_core")) continue;
//        String dirNorm = file.getAbsolutePath().replace("\\", "/");
//        context.eval("python", "sys.path.append(\"" + dirNorm + "/\")");
//        context.eval("python", "print(\"" + dirNorm + "/\")");
//      }
//      File outFile = new File("build/py_pip/pip_out");
//      outFile.mkdirs();
//      String outFileNormalized = outFile.getAbsolutePath().replace("\\", "/");
//      context.eval("python", "sys.path.append(\"" + outFileNormalized + "/\")");

      System.err.println(new File("abc").getAbsolutePath());
      File vfs = new File("C:/Users/alexl/AppData/Local/Temp/graalpy/build/generated/graalpy/resources/org.graalvm.python.vfs/venv/Lib/site-packages");
      Assertions.assertTrue(vfs.isDirectory());
      String vfsNormalized = vfs.getAbsolutePath().replace("\\", "/");
      context.eval("python", "sys.path.append(\"" + vfsNormalized + "/\")");


      //      context.eval("python", "print(sys.path)");
//      context.eval("python", "print('Installing pydantic_core wheel')");
//      File whl = new File("src/test/resources/pypi/pydantic_core-2.10.1-graalpy311-graalpy242_311_native-manylinux_2_28_x86_64.whl");
//      Assertions.assertTrue(whl.exists());
//      context.eval("python", "import subprocess; x=subprocess.run(['pip', 'install', '-t', '" + outFileNormalized +"', '"+whl.getAbsolutePath().replace("\\", "/")+"'], capture_output=True); print(x.stdout); print(x.stderr)");
//
//      context.eval("python", "print('Installing dependencies')");
//      context.eval("python", "import subprocess; x=subprocess.run(['pip', 'install', '-t', '" + outFileNormalized +"', 'pydantic==2.10.1'], capture_output=True); print(x.stdout); print(x.stderr)");
      // install

      context.eval("python", "import os; print(os.getcwd())");



////      context.eval("python", "import sys; sys.executable = '/path/to/fake/python'");
//
//      context.eval("python", "import venv\n" + "venv.create('build/venv', with_pip=False)\n");
//
//      context.eval("python", "import sys; sys.argv = ['-m', 'venv', 'build/venv', '--without-pip']");
//      context.eval("python", "import runpy; runpy.run_module('venv', run_name='__main__', alter_sys=True)");
//
//      // TODO: run in venv?
//      context.eval("python", "import sys; sys.argv = ['-I', '-m', 'ensurepip']");
//      context.eval("python", "import runpy; runpy.run_module('ensurepip', run_name='__main__', alter_sys=True)");
//
//      context.eval("python", "import sys; sys.argv = ['-m', 'pip', 'install', 'pydantic==2.11.5']");
//      context.eval("python", "import runpy; runpy.run_module('pip', run_name='__main__', alter_sys=True)");

      //
//
//      // ensure pip is installed
////      context.eval("python", "import sys; sys.argv = ['-m', 'ensurepip']");
////      context.eval("python", "import runpy; runpy.run_module('ensurepip', run_name='__main__', alter_sys=True)");
//      context.eval("python", "import sys; sys.argv = ['-m', 'pip', 'install', 'pydantic']");
//      context.eval("python", "import runpy; runpy.run_module('pip', run_name='__main__', alter_sys=True)");
//
//
      context.eval("python", "print(sys.path)");
      context.eval("python", "import pydantic");
//
//      // Test importing the commandmanager
      context.eval("python", "import montigem_runtime.commandmanager");
//      Assertions.fail("Yay, you have fixed the missing pydantic error - now test some more :)");
    }
  }
}
