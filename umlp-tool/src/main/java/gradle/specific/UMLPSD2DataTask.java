package gradle.specific;

import de.monticore.lang.sd4development.SD4DevelopmentMill;
import de.monticore.lang.sd4development.SD4DevelopmentTool;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;
import standalone.com.sun.tools.sjavac.Log;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public abstract class UMLPSD2DataTask extends UMLPExternalToolTask {

  public UMLPSD2DataTask() {
    super("UMLPSD2DataTask", null);
  }

  @Override
  @TaskAction
  public void run(InputChanges inputChanges) {
    prepareRun();

    Log.info("Starting data generation from sequence diagram");

    var args = prepareToolArgumentList();

    SD4DevelopmentMill.init();
    SD4DevelopmentTool.main(args.toArray(new String[0]));
  }



  /**
   * @param f file which should be checked
   * @return if file should be used as input for UMLPTool
   */
  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".sd");
  }

  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    if (!this.getInput().isEmpty()) {
      result.add("-i");
      result.add(getInput().getAsPath());
    }

    if (!this.getSymbolDir().isEmpty()) {
      for (var symDir : this.getSymbolDir()) {
        result.add("-path");
        result.add(symDir.getAbsolutePath());
      }
    }

    return result;
  }
}
