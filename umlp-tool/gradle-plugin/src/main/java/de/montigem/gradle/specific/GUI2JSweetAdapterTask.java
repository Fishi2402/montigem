/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;

public abstract class GUI2JSweetAdapterTask extends DefaultTask {

  @Input
  @Optional
  public abstract ListProperty<String> getDomain();

  @OutputDirectory
  public abstract DirectoryProperty getJSweetAdapterDir();

  @InputDirectory
  @Optional
  public abstract DirectoryProperty getHandWrittenCodeDir();

  @Inject
  abstract public WorkerExecutor getWorkerExecutor();

  @InputFiles
  @Classpath
  public abstract ConfigurableFileCollection getClassPath();

  @TaskAction
  public void run() {
    WorkQueue workQueue = getWorkerExecutor().classLoaderIsolation(spec -> spec.getClasspath().from(getClassPath()));
    workQueue.submit(GUI2JSweetAdapterAction.class, params -> {
      params.getDomain().set(getDomain());
      params.getJSweetAdapterDir().set(getJSweetAdapterDir());
      params.getHandWrittenJSweetDir().set(getHandWrittenCodeDir());
    });
  }


}
