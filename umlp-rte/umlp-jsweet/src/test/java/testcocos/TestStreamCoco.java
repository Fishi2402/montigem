package testcocos;

import de.se_rwth.commons.logging.LogStub;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.jsweet.transpiler.SourceFile.getSourceFiles;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestStreamCoco {

  @BeforeEach
  public void testSetup() {
    LogStub.init();
    LogStub.clearFindings();
  }

  @Test
  public void testInvalidStreamUsage() throws Exception {
    File input = Paths.get("src/test/resources/testcocos/invalid/StreamUsage.java").toFile();

    JSweetTranspiler transpiler = testutil.Util.getGUITranspiler();

    ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(
        new ConsoleTranspilationHandler());
    SourceFile[] sourceFiles = getSourceFiles(input);
    transpiler.transpile(handler, sourceFiles);

    assertNotNull(sourceFiles[0]);
    assertNotNull(sourceFiles[0].getTsFile());
    assertEquals(0, handler.getErrorCount());

    assertEquals(1, LogStub.getErrorCount());
    System.out.println(LogStub.getFindings().get(0).getMsg());
  }

  @Test
  public void testInvalid() throws Exception {
    File input = Paths.get("src/test/resources/testcocos/invalid/StreamUsageInvalid.java").toFile();

    JSweetTranspiler transpiler = testutil.Util.getGUITranspiler();

    ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(
        new ConsoleTranspilationHandler());
    SourceFile[] sourceFiles = getSourceFiles(input);
    transpiler.transpile(handler, sourceFiles);

    assertNotNull(sourceFiles[0]);
    assertNotNull(sourceFiles[0].getTsFile());
    assertEquals(0, handler.getErrorCount());

    assertEquals(1, LogStub.getErrorCount());
    System.out.println(LogStub.getFindings().get(0).getMsg());
  }

  @Test
  public void testValidStreamUsage() throws Exception {
    File input = Paths.get("src/test/resources/testcocos/valid/StreamUsageValid.java").toFile();

    JSweetTranspiler transpiler = testutil.Util.getGUITranspiler();

    ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(
        new ConsoleTranspilationHandler());
    SourceFile[] sourceFiles = getSourceFiles(input);
    transpiler.transpile(handler, sourceFiles);

    assertNotNull(sourceFiles[0]);
    assertNotNull(sourceFiles[0].getTsFile());
    assertEquals(0, handler.getErrorCount());

    assertEquals(0, LogStub.getErrorCount());
  }

  @Test
  public void testClassLevelValidStreamUsage() throws Exception {
    File input = Paths.get("src/test/resources/testcocos/valid/StreamUsageClazzErased.java").toFile();

    JSweetTranspiler transpiler = testutil.Util.getGUITranspiler();

    ErrorCountTranspilationHandler handler = new ErrorCountTranspilationHandler(
        new ConsoleTranspilationHandler());
    SourceFile[] sourceFiles = getSourceFiles(input);
    transpiler.transpile(handler, sourceFiles);

    assertNotNull(sourceFiles[0]);
    assertEquals(0, handler.getErrorCount());

    assertEquals(0, LogStub.getErrorCount());
  }

}
