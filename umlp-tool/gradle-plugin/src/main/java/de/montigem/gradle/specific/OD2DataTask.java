/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific;

import de.montigem.gradle.common.MGSingleFileTask;
import de.se_rwth.commons.logging.Log;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// Task might not be a @CacheableTask since relative paths are generated for HWC-files
@Deprecated(since = "Use ODs2DataTask")
public abstract class OD2DataTask extends MGSingleFileTask {

  public OD2DataTask() {
    super("OD2DataTask", null);
  }

  @OutputDirectory
  @Optional
  public abstract DirectoryProperty getSymbolOutDir();

  @Inject
  abstract public WorkerExecutor getWorkerExecutor();


  @Override
  @TaskAction
  public void run(InputChanges inputChanges) {
    prepareRun();

    Log.info("Starting data generation from object diagram.", getName());

    Log.debug("Input file: " + getInput().getAsPath(), getName());
    Log.debug("Symbol path: " + getSymbolPath().getAsPath(), getName());
    Log.debug("Output directory: " + getOutputDir().get(), getName());
    if (getSymbolOutDir().isPresent()) {
      Log.debug("Symbol output directory: " + getSymbolOutDir().get(), getName());
    }

    List<String> args = new ArrayList<>();
    args.add("-i");
    args.add(getInput().getAsPath());
    args.add("-path");
    args.add(getSymbolPath().getAsPath());
    args.add("-o");
    args.add(getOutputDir().get().toString());
    if (getSymbolOutDir().isPresent()) {
      args.add("-s");
      args.add(getSymbolOutDir().get().toString());
    }
    WorkQueue workQueue = getWorkerExecutor().classLoaderIsolation(spec -> spec.getClasspath().from(getExtraClasspathElements()));
    String[] argsA = args.toArray(new String[0]);
    workQueue.submit(OD2DataToolAction.class, params -> params.getArgs().set(argsA));
  }

  /**
   * @param f file which should be checked
   * @return if file should be used as input for UMLPTool
   */
  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".od");
  }

  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    if (!this.getInput().isEmpty()) {
      result.add("-i");
      result.add(getInput().getAsPath());
    }

    if (!this.getSymbolPath().isEmpty()) {
      for (var symDir : this.getSymbolPath()) {
        result.add("-path");
        result.add(symDir.getAbsolutePath());
      }
    }


    return result;
  }
}
