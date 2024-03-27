/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import de.monticore.od4development.OD4DevelopmentTool;
import gradle.common.UMLPSingleFilev2Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.*;
import org.gradle.work.InputChanges;
import standalone.com.sun.tools.sjavac.Log;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

// Task might not be a @CacheableTask since relative paths are generated for HWC-files
public abstract class UMLPOD2DataTask extends UMLPSingleFilev2Task {

  public UMLPOD2DataTask() {
    super("UMLPOD2DataTask", null);
  }


  private final ConfigurableFileCollection symbolDir = getProject().getObjects().fileCollection();

  @InputFiles
  public ConfigurableFileCollection getSymbolDir() {
    return symbolDir;
  }

  public void setSymbolDir(File f){
    symbolDir.from(f);
  }

  public void setSymbolDir(Iterable<File> f){
    symbolDir.from(f);
  }
  public void setSymbolDir(Object... paths) {
    symbolDir.from(paths);
  }

  @Override
  @TaskAction
  public void run(InputChanges inputChanges) {
    prepareRun();

    Log.info("Starting data generation from object diagram.");

    Log.debug("Input file: " + getInput().getAsPath());
    Log.debug("Symbol path: " + getSymbolDir().getAsPath());
    Log.debug("Output directory: " + getOutputDir().get());

    String[] args = {"-i", getInput().getAsPath(),
                     "-s", getSymbolDir().getAsPath(),
                     "-o", getOutputDir().get().toString()};
    OD4DevelopmentTool.main(args);
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

    if (!this.getSymbolDir().isEmpty()) {
      for (var symDir : this.getSymbolDir()) {
        result.add("-path");
        result.add(symDir.getAbsolutePath());
      }
    }



    return result;
  }
}
