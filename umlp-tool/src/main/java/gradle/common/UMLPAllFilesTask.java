package gradle.common;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.gradle.api.file.Directory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;
import umlp.UMLPToolOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

abstract public class UMLPAllFilesTask extends CommonUMLPTask {

  @Override
  protected Directory getReportDirOfFile(File f) {
    return getReportDir().get();
  }

  /**
   * @return Arguments that can be used to start CLI-Tool
   */
  @Override
  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    this.getInput().getAsFileTree().getFiles().stream()
        .filter(File::isFile)
        .filter(this::isInputFile)
        .forEach(f -> {
          result.add("-" + UMLPToolOptions.INPUT.getCMD());
          result.add(handlePath.apply(f.toPath()));
        });

    result.add("-" + UMLPToolOptions.REPORTS.getCMD());
    result.add(handlePath.apply(getReportDir().get().getAsFile().toPath()));

    return result;
  }

  /**
   * Delete all outputs of previous runs
   * @throws IOException
   */
  protected void cleanOutputs() throws IOException {
    FileUtils.deleteDirectory(getOutputDir().get().getAsFile());
    FileUtils.deleteDirectory(getReportDir().get().getAsFile());
  }

  @TaskAction
  public void run(InputChanges inputChanges) throws IOException {
    prepareRun();

    boolean shouldRunAgain = true;
    if (shouldRunAgain) {
      cleanOutputs();

      Path cwd = getProject().getProjectDir().toPath().toAbsolutePath();
      Log.warn("Starting UMLPTool: \n" +
          "\t  java -jar UMLPTool.jar " +
          String.join(" ", createArgList(p -> "." + File.separator + cwd.relativize(p)))
      );

      List<String> args = createArgList(p -> p.toAbsolutePath().toString());
      startGeneration(args);
    }
  }
}
