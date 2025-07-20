package transpilationtests;

import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.jsweet.transpiler.SourceFile.getSourceFiles;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestListTranspilation {

  @Test
  public void testArrayListDomainConfigTranspilation() throws Exception {
    File input = Paths.get("src/test/resources/java/list/TestArrayListTranspilation.java").toFile();

    JSweetTranspiler transpiler = testutil.Util.getDomainTranspiler();

    ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(
        new ConsoleTranspilationHandler());
    SourceFile[] sourceFiles = getSourceFiles(input);
    transpiler.transpile(handler, sourceFiles);
    assertNotNull(sourceFiles[0]);
    assertNotNull(sourceFiles[0].getTsFile());
    assertEquals(0, handler.getErrorCount());
  }

  @Test
  public void testArrayListGUIConfigTranspilation() throws Exception {
    File input = Paths.get("src/test/resources/java/list/TestArrayListTranspilation.java").toFile();

    JSweetTranspiler transpiler = testutil.Util.getGUITranspiler();

    ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(
        new ConsoleTranspilationHandler());
    transpiler.transpile(handler, getSourceFiles(input));
    SourceFile[] sourceFiles = getSourceFiles(input);
    transpiler.transpile(handler, sourceFiles);
    assertNotNull(sourceFiles[0]);
    assertNotNull(sourceFiles[0].getTsFile());
    assertEquals(0, handler.getErrorCount());
  }
}
