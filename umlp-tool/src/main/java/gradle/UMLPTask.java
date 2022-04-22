package gradle;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Incubating;
import org.gradle.api.file.*;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.*;
import org.gradle.work.ChangeType;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class UMLPTask extends DefaultTask {

  @Incremental
  @org.gradle.api.tasks.Optional
  @InputDirectory
  abstract DirectoryProperty getInputDir();

  @org.gradle.api.tasks.Optional
  @InputFile
  @Incremental
  @Incubating // Use `inputDir` instead!
  abstract RegularFileProperty getInputFile();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @Incremental
  abstract DirectoryProperty getHwcDir();

  @InputFile
  @org.gradle.api.tasks.Optional
  abstract RegularFileProperty getScript();

  @Input
  @org.gradle.api.tasks.Optional
  abstract ListProperty<String> getMoreArgs();

  @OutputDirectory
  abstract DirectoryProperty getOutputDir();

  @OutputDirectory
  abstract DirectoryProperty getReportDir();

  @Inject
  abstract public WorkerExecutor getWorkerExecutor();

  private WorkQueue workQueue;

  public UMLPTask() {
    super();

    // Report directory is required for incremental check
    this.getReportDir().convention(
        getProject().getLayout().getBuildDirectory().dir("umlp_reports/task_" + this.getName())
    );
  }


  protected void checkParameters() throws IllegalArgumentException {
    if (getMoreArgs().isPresent()) {
      List<String> arg = this.getMoreArgs().get();
      if (arg.contains("-i") || arg.contains("-o") || arg.contains("-hwc")) {
        throw new IllegalArgumentException("'moreArgs' must not contain '-i' / '-o' or '-hwc' flag. Use UMLPTask parameter instead");
      }
    }

    if (getScript().isPresent()) {
      throw new IllegalArgumentException("<script> not yet supported");
    }

    if (getInputFile().isPresent() && !isInputFile(this.getInputFile().getAsFile().get())) {
      throw new IllegalArgumentException("<inputFile> must be an umlp-file");
    }
  }

  /**
   * @param filePath Path of InputFile
   * @return Arguments that can be used to start CLI-Tool
   */
  protected List<String> createArgList(Path filePath) {
    assert (filePath.toFile().isFile());
    assert (isInputFile(filePath.toFile()));

    List<String> result = new ArrayList<>();
    result.add("-i");
    result.add(filePath.toString());

    result.add("-o");
    result.add(this.getOutputDir().getAsFile().get().getPath());

    result.add("-r");
    result.add(this.getReportDir().getAsFile().get().getPath());

    if (this.getHwcDir().isPresent()) {
      result.add("-hwc");
      result.add(this.getHwcDir().getAsFile().get().getPath());
    }

    if (this.getMoreArgs().isPresent()) {
      result.addAll(this.getMoreArgs().get());
    }

    return result;
  }


  /**
   * @param f File, which is used as tool-input
   * @return Sub-Directory in <ReportDir>, where reports for input file should be stored
   */
  protected Directory getReportDirOfFile(File f) {
    String pathName = f.getName();
    if (getInputDir().isPresent()) {
      Path dirPath = getInputDir().get().getAsFile().toPath();
      Path filePath = f.toPath();
      pathName = dirPath.relativize(filePath).toString();
    }
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


  protected void runOnSingleInputFile(File f, InputChanges inputChanges) {
    assert f.exists();
    assert f.isFile();
    assert isInputFile(f);

    IncGenData lastRun = new IncGenData(getIncGenFile(f));

    if (!lastRun.isUpToDate(inputChanges)) {
      Log.info(f.getName() + " is *NOT* UP-TO-DATE, starting generation process", getName());

      deletePreviousOutput(f, lastRun);

      workQueue.submit(UMLPAction.class, param -> {
        param.getArgs().set(createArgList(f.toPath()));
      });
    } else {
      Log.info(f.getName() + " is UP-TO-DATE, no action required", getName());
    }
  }

  protected boolean isInputFile(File f) {
    return f.getName().endsWith(".umlp");
  }

  @TaskAction
  public void run(InputChanges inputChanges) throws IOException {
    Log.info("Start UMLPTask", this.getName());

    checkParameters();
    workQueue = getWorkerExecutor().classLoaderIsolation();

    // Remove Artifacts from deleted inputs
    inputChanges.getFileChanges(this.getInputDir()).forEach(ch -> {
      if (ch.getChangeType() == ChangeType.REMOVED
          && isInputFile(ch.getFile())
          && ch.getFileType() == FileType.FILE) {
        this.deletePreviousOutput(ch.getFile(), new IncGenData(getIncGenFile(ch.getFile())));
      }
    });


    // Generate from InputDir
    if (getInputDir().isPresent()) {
      assert (getInputDir().getAsFile().get().isDirectory());
      getInputDir().getAsFileTree().getFiles().stream()
          .filter(this::isInputFile)
          .forEach(x -> runOnSingleInputFile(x, inputChanges));
    }

    // Generate from InputFile
    if (this.getInputFile().isPresent()) {
      runOnSingleInputFile(this.getInputFile().get().getAsFile(), inputChanges);
    }
  }
}