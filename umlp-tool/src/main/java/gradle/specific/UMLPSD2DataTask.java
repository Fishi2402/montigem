package gradle.specific;

import de.monticore.lang.sd4development.SD4DevelopmentTool;
import gradle.common.UMLPSingleFileTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;
import standalone.com.sun.tools.sjavac.Log;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class UMLPSD2DataTask extends UMLPSingleFileTask {

  private final ConfigurableFileCollection symbolDir = getProject().getObjects().fileCollection();

  public UMLPSD2DataTask() {
    super("UMLPSD2DataTask", null);
  }

  /**
   * The artifact path for imported symbols
   */
  @InputFiles
  public ConfigurableFileCollection getSymbolDir() {
    return symbolDir;
  }

  public void setSymbolDir(File f) {
    symbolDir.from(f);
  }

  public void setSymbolDir(Iterable<File> f) {
    symbolDir.from(f);
  }

  public void setSymbolDir(Object... paths) {
    symbolDir.from(paths);
  }

  @Override
  @TaskAction
  public void run(InputChanges inputChanges) {
    prepareRun();

    Log.info("Starting data generation from sequence diagram");

    var args = new ArrayList<String>(6);

    var inputPath = getInput().getAsPath();
    Log.debug("Input file: " + inputPath);
    args.addAll(List.of("--input", inputPath));

    var symbolPath = getSymbolDir().getAsPath();
    Log.debug("Symbol path: " + symbolPath);
    args.addAll(List.of("-path", symbolPath));

    var outputDirProperty = getOutputDir();
    if (outputDirProperty.isPresent()) {
      var outputDirectory = outputDirProperty.get().toString();
      Log.debug("Output directory: " + outputDirectory);
      args.addAll(List.of("--output", outputDirectory));
      args.addAll(List.of("--symboltable", outputDirectory));
    }

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
