/* (c) https://github.com/MontiCore/monticore */
package gradle.merge;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;

public abstract class UMLPMergeTask extends SourceTask {

  @OutputFile
  public abstract RegularFileProperty getOutputFile();

  @Inject
  abstract public WorkerExecutor getWorkerExecutor();


  @TaskAction
  public void run() {
    WorkQueue workQueue = getWorkerExecutor().classLoaderIsolation();
    workQueue.submit(MergeAction.class, param -> {
      param.getInputs().from(this.getSource());
      param.getOutputFile().set(this.getOutputFile());
    });
  }


}
