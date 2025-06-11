package transpilationtests;

import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.jsweet.transpiler.SourceFile.getSourceFiles;

public class TestStreamTranspilation {

  @Test
  public void testJ4TsStreams() throws Exception {
    File input = new File("src/test/resources/test/java/streams/J4TsStream.java");
    JSweetTranspiler transpiler = testutil.Util.getDomainTranspiler();

    transpiler.transpile(new ConsoleTranspilationHandler(), getSourceFiles(input));
  }
}
