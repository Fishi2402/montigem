/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
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

public abstract class UMLPJava2TypeScriptConfigTask extends DefaultTask {

  @Input
  public abstract Property<String> getDomainModel();

  @Input
  @Optional
  public abstract Property<Boolean> getGenerateForDomain();

  @Input
  @Optional
  public abstract Property<Boolean> getGenerateForGui();

  @OutputDirectory
  public abstract DirectoryProperty getJsweetConfigOut();

  @Optional
  @InputDirectory
  public abstract DirectoryProperty getHwcDir();

  @Inject
  abstract public WorkerExecutor getWorkerExecutor();

  @InputFiles
  @Classpath
  public abstract ConfigurableFileCollection getClassPath();

  @TaskAction
  public void run() {
    WorkQueue workQueue = getWorkerExecutor().classLoaderIsolation(spec -> spec.getClasspath().from(getClassPath()));
    workQueue.submit(UMLPJava2TypeScriptConfigAction.class, params -> {
      params.getDomainModel().set(getDomainModel());
      params.getGenerateForDomain().set(getGenerateForDomain());
      params.getGenerateForGui().set(getGenerateForGui());
      params.getJsweetConfigOut().set(getJsweetConfigOut());
      params.getHwcDir().set(getHwcDir());
    });
  }


}
