package transpilationtests;

import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.jsweet.transpiler.SourceFile.getSourceFiles;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestListTranspilation {

  @Test
  public void testArrayListDomainConfigTranspilation() throws Exception {
    File input = new File("src/test/resources/java/list/TestArrayListTranspilation.java");

    JSweetTranspiler transpiler = testutil.Util.getDomainTranspiler();

    ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(
        new ConsoleTranspilationHandler());
    transpiler.transpile(handler, getSourceFiles(input));

    assertEquals(0, handler.getErrorCount());
  }

  @Test
  public void testArrayListGUIConfigTranspilation() throws Exception {
    File input = new File("src/test/resources/java/list/TestArrayListTranspilation.java");

    JSweetTranspiler transpiler = testutil.Util.getGUITranspiler();

    ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(
        new ConsoleTranspilationHandler());
    transpiler.transpile(handler, getSourceFiles(input));

    assertEquals(0, handler.getErrorCount());
  }
}
