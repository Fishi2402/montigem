/* (c) https://github.com/MontiCore/monticore */
package gradle;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.*;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.work.ChangeType;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;
import umlp.UMLPTool;
import umlp.UMLPToolOptions;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@CacheableTask
public abstract class UMLPTask extends DefaultTask {
  final String UMLP_TASK_DEBUG = "de.rwth.se.umlp.debug";
  final String ORG_GRADLE_PARALLEL = "org.gradle.parallel";

  private final ConfigurableFileCollection input = getProject().getObjects().fileCollection();

  @SkipWhenEmpty    // Implies @Incremental. Do nothing if no input model exists
  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  @IgnoreEmptyDirectories
  public ConfigurableFileCollection getInput(){
    return input; // TODO: FileTree & Matching?
  }

  public void setInput(File f){
    input.from(f);
  }

  public void setInput(Iterable<File> f){
    input.from(f);
  }

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental    // No full rebuild, when only HWC-Directory is changed. Requires logic in Task Execution!
  @IgnoreEmptyDirectories
  public abstract DirectoryProperty getHwcDir();

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
  public abstract ListProperty<String> getMoreArgs();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getDebug();

  @OutputDirectory
  public abstract DirectoryProperty getOutputDir();

  @OutputDirectory
  public abstract DirectoryProperty getReportDir();

  @Inject
  protected abstract WorkerExecutor getWorkerExecutor();

  private WorkQueue workQueue;

  public UMLPTask() {
    super();

    // Report directory is required for incremental check
    this.getReportDir().convention(
        getProject().getLayout().getBuildDirectory().dir("umlp_reports/task_" + this.getName())
    );

    // Project wide setting of debug modus
    if(getProject().hasProperty(UMLP_TASK_DEBUG)){
      boolean debug = "true" .equals(getProject().property(UMLP_TASK_DEBUG));
      getDebug().convention(debug);

      // The debug-mode disables isolation... hence static variables are shared and errors can occur, especially in parallel execution.
      if(debug && getProject().hasProperty(ORG_GRADLE_PARALLEL) && "true".equals(getProject().property(ORG_GRADLE_PARALLEL))){
        Log.warn("Gradle Parallel Execution should be disabled in UMLP Debug Mode. \n" +
            "set\n\t" + ORG_GRADLE_PARALLEL + "=false\n in your <gradle.properties>" );
      }
    } else {
      // Default: No Debug
      getDebug().convention(false);
    }
    this.getOutputs().doNotCacheIf("Do not cache when debugging is enabled",
        (task) ->  ((UMLPTask)task).getDebug().get());
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

    List<String> result = new ArrayList<>();
    result.add("-i");
    result.add(handlePath.apply(filePath));

    result.add("-o");
    result.add(handlePath.apply(getOutputDir().getAsFile().get().toPath()));

    result.add("-r");
    result.add(handlePath.apply(getReportDirOfFile(filePath.toFile()).getAsFile().toPath()));

    result.add("-rb");
    result.add(handlePath.apply(getProject().getProjectDir().toPath()));

    if(this.getHwcDir().isPresent()) {
      result.add("-hwc");
      result.add(handlePath.apply(getHwcDir().getAsFile().get().toPath()));
    }

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

    if(this.getDebug().get()){
      // Set the "debug" flag in the arguments
      result.add("-" + UMLPToolOptions.DEBUG.getCMD());
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
    if (getDebug().get()){
      // In debug mode, run code directly. Otherwise breakpoints etc do not work
      UMLPTool.main(args.toArray(new String[0]));
    } else {
      // In normal mode, run in a workQue. This ensures isolation & parallelization
      workQueue.submit(UMLPAction.class, param -> {
        param.getArgs().set(args);
      });
    }
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
