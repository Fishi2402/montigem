import de.monticore.doc.MontigemDocTool;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("latex")
public class LaTeXCompilerTest {

  public final static String OUTPUT_PATH = "build/test-output/";

  @Test
  public void testFilesCompile() throws URISyntaxException {
    String outLatex = OUTPUT_PATH + "latex";
    URL resourceUrl = getClass().getClassLoader().getResource("");
    assertNotNull(resourceUrl, "resource folder not found");
    Path resource = Paths.get(resourceUrl.toURI());

    String[] args = {
        "-i", resource.resolve("lib").toString(),
        "--latex", outLatex,
        "--examples", resource.resolve("examples").toString(),
        "--images", resource.resolve("images").toString(),
        "--hwc", resource.resolve("hwc").toString(),
        "--cd", resource.resolve("lib").toString(),
        "--preproc", "true"
    };
    MontigemDocTool.main(args);
    Path buildPath = Paths.get(outLatex);

    Path mainTexFile = buildPath.resolve("main.tex");

    if (!Files.exists(mainTexFile)) {
      fail("main.tex file not found in " + buildPath);
    }

    File file = mainTexFile.toFile();
    String texFile = file.getAbsolutePath();

    CompilationResult canCompile = compileWithOutput(texFile);

    if (!canCompile.isSuccess()) {
      fail("Compilation failed for " + texFile + "\nOutput: " + canCompile.getOutput());
    }
  }

  /**
   * Clean up auxiliary files that might interfere with compilation
   */
  private static void cleanupAuxiliaryFiles(String texFilePath) {
    String baseName = texFilePath.substring(0, texFilePath.lastIndexOf('.'));
    String[] extensions = {".aux", ".log", ".pdf", ".fls", ".fdb_latexmk", ".synctex.gz"};

    for (String ext : extensions) {
      File auxFile = new File(baseName + ext);
      if (auxFile.exists()) {
        auxFile.delete();
      }
    }
  }

  /**
   * Gets compilation output (useful for debugging)
   *
   * @param texFilePath Path to the .tex file
   * @return CompilationResult containing success status and output
   */
  public static CompilationResult compileWithOutput(String texFilePath) {
    try {
      File texFile = new File(texFilePath);

      // Clean up first
      cleanupAuxiliaryFiles(texFilePath);

      ProcessBuilder pb = new ProcessBuilder(
          "pdflatex",
          "-interaction=nonstopmode",
          "-file-line-error",
          "-output-directory=" + texFile.getParent(),
          texFile.getAbsolutePath()
      );

      pb.directory(texFile.getParentFile());
      pb.redirectErrorStream(true);

      Process process = pb.start();

      // Capture all output
      StringBuilder output = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getInputStream()))) {

        String line;
        while ((line = reader.readLine()) != null) {
          output.append(line).append("\n");
        }
      }

      // Wait for process completion
      boolean finished = process.waitFor(60, TimeUnit.SECONDS);

      if (!finished) {
        process.destroyForcibly();
        output.append("\n[TIMEOUT] Process was terminated due to timeout");
      }

      // Give file system time to sync
      Thread.sleep(1000);

      // Check if PDF was created
      String baseName = texFilePath.substring(0, texFilePath.lastIndexOf('.'));
      String pdfPath = baseName + ".pdf";
      boolean pdfCreated = new File(pdfPath).exists();

      return new CompilationResult(pdfCreated, output.toString());

    } catch (Exception e) {
      return new CompilationResult(false, "Error: " + e.getMessage());
    }
  }

  public static class CompilationResult {
    private final boolean success;
    private final String output;

    public CompilationResult(boolean success, String output) {
      this.success = success;
      this.output = output;
    }

    public boolean isSuccess() {
      return success;
    }

    public String getOutput() {
      return output;
    }
  }
}