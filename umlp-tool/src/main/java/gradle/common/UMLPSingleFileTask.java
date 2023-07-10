package gradle.common;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileType;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.ChangeType;
import org.gradle.work.InputChanges;
import umlp.UMLPToolOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

abstract public class UMLPSingleFileTask extends CommonUMLPTask {
  /**
   *
   * @param f file which should be checked
   * @return if file should be used as input for UMLPTool
   */
  public boolean isInputFile(File f){
    return true;
  }

  /**
   * @param f File, which is used as tool-input
   * @return Sub-Directory in <ReportDir>, where reports for input file should be stored
   */
  protected Directory getReportDirOfFile(File f) {
    String pathName = f.getName();
    return getReportDir().dir(pathName.replace(".", "__")).get();
  }

  /**
   * @param inputFile File, which is used as tool-input
   * @return Generated 'IncGenGradleCheck' file from previous run. File might not always exist.
   */
  protected File getIncGenFile(File inputFile) {
    return getReportDirOfFile(inputFile).dir(
        inputFile.getName().toLowerCase().replace(".", "__")
    ).file("IncGenGradleCheck.txt").getAsFile();
  }

  protected void deletePreviousOutput(File f, IncGenData lastRun) {
    Log.info("Deleting generated artifacts from previous run of " + f.getName(), this.getName());
    lastRun.deleteGeneratedFiles();

    // Deleting old reports.
    try {
      FileUtils.deleteDirectory(this.getReportDirOfFile(f).getAsFile());
    } catch (IOException e) {
      Log.warn("Error deleting old reports. " +
          "This could cause errors in upcoming incremental builds." +
          "Affected directory: " + this.getReportDirOfFile(f));
    }
  }

  protected void runOnSingleInputFile(File f, InputChanges changedInput) {
    assert f.exists();
    assert f.isFile();
    assert isInputFile(f);

    IncGenData lastRun = new IncGenData(getIncGenFile(f), this.getProject().getProjectDir());

    if (!changedInput.isIncremental()  || !lastRun.isUpToDate(getStreamOfChanges(changedInput))) {
      Log.warn(f.getName() + " is *NOT* UP-TO-DATE, starting generation process");

      deletePreviousOutput(f, lastRun);
      startGeneration(f);
    } else {
      Log.warn(f.getName() + " is UP-TO-DATE, no action required");
    }
  }

  private void startGeneration(File f) {
    Path cwd = getProject().getProjectDir().toPath().toAbsolutePath();
    Log.warn("Starting UMLPTool: \n" +
        "\t  java -jar UMLPTool.jar " +
        String.join(" ", createArgList(f.toPath(), p -> "." + File.separator + cwd.relativize(p)))
    );

    List<String> args = createArgList(f.toPath(), p -> p.toAbsolutePath().toString());
    super.startGeneration(args);
  }

  /**
   * @param filePath Path of InputFile
   * @return Arguments that can be used to start CLI-Tool
   */
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    assert (filePath.toFile().isFile());
    assert (isInputFile(filePath.toFile()));

    List<String> result = super.createArgList(handlePath);

    result.add("-" + UMLPToolOptions.INPUT.getCMD());
    result.add(handlePath.apply(filePath));

    result.add("-" + UMLPToolOptions.REPORTS.getCMD());
    result.add(handlePath.apply(getReportDirOfFile(filePath.toFile()).getAsFile().toPath()));

    return result;
  }

  @TaskAction
  public void run(InputChanges inputChanges) {
    Log.info("Starting Task", this.getName());
    prepareRun();

    // Remove Artifacts from deleted inputs
    inputChanges.getFileChanges(this.getInput()).forEach(ch -> {
      if (ch.getChangeType() == ChangeType.REMOVED
          && isInputFile(ch.getFile())
          && ch.getFileType() == FileType.FILE) {
        this.deletePreviousOutput(ch.getFile(), new IncGenData(getIncGenFile(ch.getFile()), getProject().getProjectDir().toPath().toAbsolutePath().toFile()));
      }
    });

    // Generate from input
    getInput().getAsFileTree().getFiles().stream()
        .filter(this::isInputFile)    // TODO: match directly on "getInput()"
        .forEach(x -> runOnSingleInputFile(x, inputChanges));
  }

}
