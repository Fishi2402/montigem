package transpilationtests;

import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.jsweet.transpiler.SourceFile.getSourceFiles;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestStreamTranspilation {

  @Test
  public void testJ4TsStreams() throws Exception {
    File input = Paths.get("src/test/resources/java/streams/J4TsStream.java").toFile();
    JSweetTranspiler transpiler = testutil.Util.getDomainTranspiler();

    SourceFile[] sourceFiles = getSourceFiles(input);
    transpiler.transpile(new ConsoleTranspilationHandler(), sourceFiles);
    assertNotNull(sourceFiles[0]);
    assertNotNull(sourceFiles[0].getTsFile());
  }
}
