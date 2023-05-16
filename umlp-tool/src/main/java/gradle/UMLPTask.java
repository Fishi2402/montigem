/* (c) https://github.com/MontiCore/monticore */
package gradle;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.gradle.api.file.*;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.work.ChangeType;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import umlp.UMLPTool;
import umlp.UMLPToolOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@CacheableTask
public abstract class UMLPTask extends CommonUMLPTask {

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental
  public abstract DirectoryProperty getModelDir();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental
  public abstract DirectoryProperty getSymbolDir();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @IgnoreEmptyDirectories
  // Do not add "@Incremental" (unless you really know what you are doing)
  public abstract DirectoryProperty getTmplDir();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<String> getConfigTemplate();


  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getCdToGui();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getForClient();


  public UMLPTask() {
    super();

    getCdToGui().convention(false);
    getForClient().convention(false);
  }


  protected void checkParameters() throws IllegalArgumentException {
    if (getMoreArgs().isPresent()) {
      List<String> arg = this.getMoreArgs().get();
      if (arg.contains("-i") || arg.contains("-o") || arg.contains("-hwc")) {
        throw new IllegalArgumentException("'moreArgs' must not contain '-i' / '-o' or '-hwc' flag. Use UMLPTask parameter instead");
      }
    }
  }

  /**
   * @param filePath Path of InputFile
   * @return Arguments that can be used to start CLI-Tool
   */
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    assert (filePath.toFile().isFile());
    assert (isInputFile(filePath.toFile()));

    List<String> result = super.createArgList(handlePath);
    result.add("-i");
    result.add(handlePath.apply(filePath));

    result.add("-r");
    result.add(handlePath.apply(getReportDirOfFile(filePath.toFile()).getAsFile().toPath()));

    if(this.getModelDir().isPresent()) {
      result.add("-path");
      result.add(handlePath.apply(getModelDir().getAsFile().get().toPath()));
      if (this.getSymbolDir().isPresent()) {
        result.add(handlePath.apply(getSymbolDir().getAsFile().get().toPath()));
      }
    }

    if(this.getTmplDir().isPresent()) {
      result.add("-fp");
      result.add(handlePath.apply(getTmplDir().getAsFile().get().toPath()));
    }

    if(this.getConfigTemplate().isPresent()){
      result.add("-ct");
      result.add(getConfigTemplate().get());
    }

    if(this.getCdToGui().get()){
      result.add("-" + UMLPToolOptions.CD2GUI.getCMD());
    }

    if(this.getForClient().get()){
      result.add("-" + UMLPToolOptions.FOR_CLIENT.getCMD());
    }

    return result;
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

  @Internal
  protected Set<Provider<? extends FileSystemLocation>> getOtherInputDirectories(){
    return Set.of(getHwcDir(), getModelDir(), getSymbolDir());
  }
  private Stream<FileChange> getStreamOfChanges(InputChanges inputChanges) {
    Stream<FileChange> result = StreamSupport.stream(inputChanges.getFileChanges(getInput()).spliterator(), false);

    for(Provider<? extends FileSystemLocation> inputLocation: getOtherInputDirectories()){
      result = Stream.concat(result, StreamSupport.stream(inputChanges.getFileChanges(inputLocation).spliterator(), false));
    }
    return result;
  }

  protected boolean isInputFile(File f) {
    return f.getName().endsWith(".umlp") || f.getName().endsWith(".gui");
  }

  @TaskAction
  public void run(InputChanges inputChanges) {
    Log.info("Start UMLPTask", this.getName());
    if(getDebug().get()){
      Log.warn("Task is running in debug modus and isolation is disabled. This can lead to errors, especially when two UMLPTasks are running parallel. \n" +
          "Deactivate debug modus once debugging is finished!");
    }

    checkParameters();
    workQueue = getWorkerExecutor().classLoaderIsolation();

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
