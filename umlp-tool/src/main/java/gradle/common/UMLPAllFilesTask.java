package gradle.common;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.gradle.api.file.Directory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;
import umlp.UMLPToolOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

abstract public class UMLPAllFilesTask extends CommonUMLPTask {

  @Override
  protected Directory getReportDirOfFile(File f) {
    return getReportDir().get();
  }

  @Internal
  protected Stream<File> getInputFilesAsStream() {
    return this.getInput().getAsFileTree().getFiles().stream()
        .filter(File::isFile)
        .filter(this::isInputFile);
  };


  /**
   * @return Arguments that can be used to start CLI-Tool
   */
  @Override
  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    getInputFilesAsStream()
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

  protected boolean shouldRunAgain(InputChanges inputChanges){
    return getInputFilesAsStream()
        .map(this::getIncGenFile)
        .map(incGenFile -> new IncGenData(incGenFile, this.getProject().getProjectDir()))
        .map(incGenData -> incGenData.isUpToDate(getStreamOfChanges(inputChanges)))
        .anyMatch(upToDate -> !upToDate);
  }

  @TaskAction
  public void run(InputChanges inputChanges) throws IOException {
    prepareRun();

    boolean shouldRunAgain = !inputChanges.isIncremental() || shouldRunAgain(inputChanges);
    if (shouldRunAgain) {
      Log.warn("*NOT* UP-TO-DATE, starting generation process");

      cleanOutputs();

      Path cwd = getProject().getProjectDir().toPath().toAbsolutePath();
      Log.warn("Starting UMLPTool: \n" +
          "\t  java -jar UMLPTool.jar " +
          String.join(" ", createArgList(p -> "." + File.separator + cwd.relativize(p)))
      );

      List<String> args = createArgList(p -> p.toAbsolutePath().toString());
      startGeneration(args);
    } else {
      Log.warn("UP-TO-DATE, no action required");
    }
  }
}
