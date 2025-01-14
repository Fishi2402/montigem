/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import de.se_rwth.commons.logging.Log;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;

// Task might not be a @CacheableTask since relative paths are generated for HWC-files
public abstract class UMLPOD2DataTask extends UMLPExternalToolTask {

  public UMLPOD2DataTask() {
    super("UMLPOD2DataTask", null);
  }

  @Inject
  abstract public WorkerExecutor getWorkerExecutor();

  @Override
  @TaskAction
  public void run(InputChanges inputChanges) {
    prepareRun();

    Log.info("Starting data generation from object diagram.", getName());

    var args = prepareToolArgumentList();

    WorkQueue workQueue = getWorkerExecutor().classLoaderIsolation(spec -> spec.getClasspath().from(getExtraClasspathElements()));
    workQueue.submit(OD2DataToolAction.class, params -> params.getArgs().set(args.toArray(new String[0])));
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".od");
  }
}
