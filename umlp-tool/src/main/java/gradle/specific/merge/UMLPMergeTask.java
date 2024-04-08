/* (c) https://github.com/MontiCore/monticore */
package gradle.specific.merge;

import de.monticore.gradle.common.CommonMCTask;
import de.se_rwth.commons.logging.Log;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;

public abstract class UMLPMergeTask extends SourceTask {

  @OutputFile
  public abstract RegularFileProperty getOutputFile();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getDebug();

  @Inject
  abstract public WorkerExecutor getWorkerExecutor();

  public UMLPMergeTask() {
    CommonMCTask.setDebugConvention(getProject(), getDebug());
  }

  @TaskAction
  public void run() {
    if(getDebug().get()){
      Log.warn("Task is running in debug modus and isolation is disabled. This can lead to errors, especially when two UMLPTasks are running parallel. \n" +
          "Deactivate debug modus once debugging is finished!");

      MergeAction.umlpMerge(getSource().getFiles(), getOutputFile().getAsFile().get());
    }else {
      WorkQueue workQueue = getWorkerExecutor().classLoaderIsolation();
      workQueue.submit(MergeAction.class, param -> {
        param.getInputs().from(this.getSource());
        param.getOutputFile().set(this.getOutputFile());
      });
    }
  }


}
