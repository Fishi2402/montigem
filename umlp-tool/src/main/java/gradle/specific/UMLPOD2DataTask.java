/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import de.monticore.od4development.OD4DevelopmentTool;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;
import standalone.com.sun.tools.sjavac.Log;

import java.io.File;

// Task might not be a @CacheableTask since relative paths are generated for HWC-files
public abstract class UMLPOD2DataTask extends UMLPExternalToolTask {

  public UMLPOD2DataTask() {
    super("UMLPOD2DataTask", null);
  }

  @Override
  @TaskAction
  public void run(InputChanges inputChanges) {
    prepareRun();

    Log.info("Starting data generation from object diagram.");

    var args = prepareToolArgumentList();

    OD4DevelopmentTool.main(args.toArray(new String[0]));
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".od");
  }
}
