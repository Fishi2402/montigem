/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific.serialize;

import de.montigem.gradle.specific.cd.CDGeneratorAction;
import de.se_rwth.commons.logging.Log;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// to be replaced by UMLPSymbolsTask
@Deprecated
abstract public class CD2SymTask extends DefaultTask {

  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract DirectoryProperty getInput();

  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  @Optional
  public abstract DirectoryProperty getSymbolPath();

  @OutputDirectory
  public abstract DirectoryProperty getOutput();

  public CD2SymTask() {
    super();
  }

  @Inject
  abstract public WorkerExecutor getWorkerExecutor();

  @InputFiles
  @Classpath
  public abstract ConfigurableFileCollection getClassPath();

  @TaskAction
  public void run() {
    WorkQueue workQueue = getWorkerExecutor().classLoaderIsolation(spec -> spec.getClasspath().from(getClassPath()));
    // TODO: Isolate TaskAction, so that no side-effects (... static variables / Logger / Mill) occur
    // Also important for parallelization

    Set<File> inputFiles = getInput().getAsFileTree().getFiles().stream()
        .filter(File::isFile)
        .filter(f -> f.getName().endsWith(".cd") || f.getName().endsWith(".umlp"))
        .collect(Collectors.toUnmodifiableSet());

    for (File inputFile : inputFiles) {
      List<String> args = new ArrayList<>();
      args.add("-i");
      args.add(inputFile.getAbsolutePath());

      if (getSymbolPath().isPresent()) {
        args.add("-path");
        args.add(getSymbolPath().get().getAsFile().getAbsolutePath());
      }

      args.add("-s");
      args.add(getOutput().getAsFile().get().getAbsolutePath());

      args.add("-c2mc");

      Log.trace(args.toString(), this.getName());

      String[] argsA = args.toArray(new String[0]);
      workQueue.submit(CDGeneratorAction.class, cdGenParams -> {
        cdGenParams.getArgs().set(argsA);
      });

    }
  }
}
