package gradle;

import de.se_rwth.commons.logging.Log;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.IgnoreEmptyDirectories;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.work.Incremental;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;
import umlp.UMLPTool;
import umlp.UMLPToolOptions;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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




  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental
  // No full rebuild, when only HWC-Directory is changed. Requires logic in Task Execution!
  @IgnoreEmptyDirectories
  public abstract DirectoryProperty getHwcDir();


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

  protected WorkQueue workQueue;

  public CommonUMLPTask(){
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

  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = new ArrayList<>();

    result.add("-o");
    result.add(handlePath.apply(getOutputDir().getAsFile().get().toPath()));

    result.add("-rb");
    result.add(handlePath.apply(getProject().getProjectDir().toPath()));

    if(this.getHwcDir().isPresent()) {
      result.add("-hwc");
      result.add(handlePath.apply(getHwcDir().getAsFile().get().toPath()));
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

  protected  void startGeneration( List<String> args) {
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


}
