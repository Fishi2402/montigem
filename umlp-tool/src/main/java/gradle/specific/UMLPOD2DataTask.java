/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import de.monticore.od4development.OD4DevelopmentTool;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;
import standalone.com.sun.tools.sjavac.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    var args = new ArrayList<String>(8);

    var inputPath = getInput().getAsPath();
    Log.debug("Input file: " + inputPath);
    args.addAll(List.of("--input", inputPath));

    var symbolInputPath = getSymbolDir().getAsPath();
    Log.debug("Symbol input path: " + symbolInputPath);
    args.addAll(List.of("-path", symbolInputPath));

    var symbolOutDir = getSymbolOutDir();
    if (symbolOutDir.isPresent()) {
      var symbolOutputDir = symbolOutDir.get().toString();
      Log.debug("Symbol output dir: " + symbolOutputDir);
      args.addAll(List.of("--symboltable", symbolOutputDir));
    }

    var outputDirectory = getOutputDir().get().toString();
    Log.debug("Output directory: " + outputDirectory);
    args.addAll(List.of("--output", outputDirectory));

    OD4DevelopmentTool.main(args.toArray(new String[0]));
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".od");
  }
}
