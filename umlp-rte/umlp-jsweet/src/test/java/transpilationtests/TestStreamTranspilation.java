package transpilationtests;

import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.jsweet.transpiler.SourceFile.getSourceFiles;

public class TestStreamTranspilation {

  @Test
  public void testJ4TsStreams() throws Exception {
    File input = Paths.get("src/test/resources/test/java/streams/J4TsStream.java").toFile();
    JSweetTranspiler transpiler = testutil.Util.getDomainTranspiler();

    transpiler.transpile(new ConsoleTranspilationHandler(), getSourceFiles(input));
  }
}
