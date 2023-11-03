package gradle.common;

import de.se_rwth.commons.logging.Log;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.IgnoreEmptyDirectories;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import org.gradle.workers.ClassLoaderWorkerSpec;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;
import umlp.UMLPToolOptions;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class CommonUMLPTask extends DefaultTask {
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
  public void setInput(Object... paths){
    input.from(paths);
  }




  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental  // No full rebuild, when only HWC-Directory is changed. Requires logic in Task Execution!
  @IgnoreEmptyDirectories
  public abstract DirectoryProperty getHwcDir();

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

  @InputFiles
  @Optional
  // Absolute, since this can contain elements from the gradle jar cache, outside the project
  @PathSensitive(PathSensitivity.ABSOLUTE)
  public abstract ConfigurableFileCollection getExtraClasspathElements();

  @Input
  @Optional
  public abstract ListProperty<String> getModuleConfigTemplates();

  @Inject // Used for isolated/parallel execution
  protected abstract WorkerExecutor getWorkerExecutor();
  protected WorkQueue workQueue;

  public CommonUMLPTask(){
    super();

    // Report directory is required for incremental check
    this.getReportDir().convention(
        getProject().getLayout().getBuildDirectory().dir("umlp_reports/task_" + this.getName())
    );

    // Project wide setting of debug modus
    if(getProject().hasProperty(UMLP_TASK_DEBUG)){
      boolean debug = "true".equals(getProject().property(UMLP_TASK_DEBUG));
      getDebug().convention(debug);

      // The debug-mode disables isolation... hence static variables are shared and errors can occur, especially in parallel execution.
      if(debug && getProject().hasProperty(ORG_GRADLE_PARALLEL) && "true".equals(getProject().property(ORG_GRADLE_PARALLEL))){
        Log.warn("Gradle Parallel Execution should be disabled in UMLP Debug Mode. \n" +
            "Otherwise static variables (e.g., Mills, SymbolTables) of one Task can influence other parallel Tasks!\n" +
            "set\n\t" + ORG_GRADLE_PARALLEL + "=false\n in your <gradle.properties>" );
      }
    } else {
      // Default: No Debug
      getDebug().convention(false);
    }
    this.getOutputs().doNotCacheIf("Do not cache when debugging is enabled",
        (task) ->  ((CommonUMLPTask)task).getDebug().get());
  }

  /** Create CLI-Arguments for UMLPTool. Notice that "input" parameter is not set, must be added in subclass
   *
   * @param handlePath converts Paths String-Argument. _Must_ be used for all Paths!
   *                   It is used to print relative paths OR absolute paths
   * @return Arguments that can be handed to UMLPTool.main(...)
   */
  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = new ArrayList<>();

    result.add("-" + UMLPToolOptions.OUTPUT.getCMD());
    result.add(handlePath.apply(getOutputDir().getAsFile().get().toPath()));

    result.add("-" + UMLPToolOptions.REPORT_BASE.getCMD());
    result.add(handlePath.apply(getProject().getProjectDir().toPath()));

    if(this.getTmplDir().isPresent()) {
      result.add("-" + UMLPToolOptions.TMPL_PATH.getCMD());
      result.add(handlePath.apply(getTmplDir().getAsFile().get().toPath()));
    }

    if(this.getConfigTemplate().isPresent()){
      result.add("-" + UMLPToolOptions.CFGTMPL.getCMD());
      result.add(getConfigTemplate().get());
    }

    if(getHwcDir().isPresent()) {
      result.add("-" + UMLPToolOptions.HWC_PATH.getCMD());
      result.add(handlePath.apply(getHwcDir().getAsFile().get().toPath()));
    }

    if(getDebug().get()){
      result.add("-" + UMLPToolOptions.DEBUG.getCMD());
    }

    if(getMoreArgs().isPresent()) {
      result.addAll(this.getMoreArgs().get());
    }

    if(getModuleConfigTemplates().isPresent()){
      for (String moduleConfigTemplate : getModuleConfigTemplates().get()) {
        result.add("--module-config");
        result.add(moduleConfigTemplate);
      }
    }

    return result;
  }

  protected void checkParameters() throws IllegalArgumentException {
    if (getMoreArgs().isPresent()) {
      List<String> arg = this.getMoreArgs().get();
      if (arg.contains("-" + UMLPToolOptions.INPUT.getCMD())
          || arg.contains("-" + UMLPToolOptions.OUTPUT.getCMD())
          || arg.contains("-" + UMLPToolOptions.HWC_PATH.getCMD())) {
        throw new IllegalArgumentException("'moreArgs' must not contain '-i' / '-o' or '-hwc' flag. Use Parameters instead");
      }
    }
  }

  /** Run UMLPTool with the given Arguments. Depending on "debug" this is either run in a WorkAction or directly.
   *
   * @param args Arguments for UMLPTool.main(...)
   */
  protected void startGeneration(List<String> args) {
    if (getDebug().get()){
      // In debug mode, run code directly. Otherwise, breakpoints etc. do not work
      UMLPAction.run(args.toArray(new String[0]));
    } else {
      // In normal mode, run in a workQue. This ensures isolation & parallelization
      workQueue.submit(UMLPAction.class, param -> param.getArgs().set(args));
    }
  }


  @Internal
  public Set<Provider<? extends FileSystemLocation>> getOtherInputDirectories(){
    return Set.of(getHwcDir());
  }
  protected Stream<FileChange> getStreamOfChanges(InputChanges inputChanges) {
    Stream<FileChange> result = StreamSupport.stream(inputChanges.getFileChanges(getInput()).spliterator(), false);

    for(Provider<? extends FileSystemLocation> inputLocation: getOtherInputDirectories()){
      result = Stream.concat(result, StreamSupport.stream(inputChanges.getFileChanges(inputLocation).spliterator(), false));
    }
    return result;
  }

  protected void prepareRun(){
    Log.info("Starting Task", this.getName());
    if(getDebug().get()){
      Log.warn("Task is running in debug modus and isolation is disabled. This can lead to errors, especially when two UMLPTasks are running parallel. \n" +
          "Deactivate debug modus once debugging is finished!");
    }

    checkParameters();
    workQueue = getWorkerExecutor().classLoaderIsolation(new Action<ClassLoaderWorkerSpec>() {
      @Override
      public void execute(ClassLoaderWorkerSpec classLoaderWorkerSpec) {
        if(!getExtraClasspathElements().isEmpty()) {
          classLoaderWorkerSpec.getClasspath().from(getExtraClasspathElements());
        }
      }
    });
  }

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
  protected abstract Directory getReportDirOfFile(File f);

  /**
   * @param inputFile File, which is used as tool-input
   * @return Generated 'IncGenGradleCheck' file from previous run. File might not always exist.
   */
  protected File getIncGenFile(File inputFile) {
    assert(isInputFile(inputFile));
    return getReportDirOfFile(inputFile).dir(
        inputFile.getName().toLowerCase().replace(".", "__")
    ).file("IncGenGradleCheck.txt").getAsFile();
  }


}
