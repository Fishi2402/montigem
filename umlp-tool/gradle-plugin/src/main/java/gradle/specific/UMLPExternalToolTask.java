package gradle.specific;

import gradle.common.UMLPSingleFileTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.jetbrains.annotations.NotNull;
import standalone.com.sun.tools.sjavac.Log;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class UMLPExternalToolTask extends UMLPSingleFileTask {

  private final DirectoryProperty symbolOutDir = getProject().getObjects().directoryProperty();
  private final ConfigurableFileCollection symbolDir = getProject().getObjects().fileCollection();

  public UMLPExternalToolTask(String type, String symbolPathConfigurationName) {
    super(type, symbolPathConfigurationName);
  }

  @InputFiles
  @Optional
  public DirectoryProperty getSymbolOutDir() {
    return symbolOutDir;
  }

  public void setSymbolOutDir(File f) {
    symbolOutDir.set(f);
  }

  public void setSymbolOutDir(Directory directory) {
    symbolOutDir.set(directory);
  }

  public void setSymbolOutDir(Provider<? extends Directory> provider) {
    symbolOutDir.set(provider);
  }

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

  protected @NotNull List<String> prepareToolArgumentList() {
    var inputPath = getInput().getAsPath();
    Log.debug("Input file: " + inputPath);
    var args = new ArrayList<>(List.of("--input", inputPath));

    var symbolPath = getSymbolDir().getAsPath();
    Log.debug("Symbol input path: " + symbolPath);
    args.addAll(List.of("-path", symbolPath));

    var symbolOutDir = getSymbolOutDir();
    if (symbolOutDir.isPresent()) {
      var symbolOutputDir = symbolOutDir.get().toString();
      Log.debug("Symbol output dir: " + symbolOutputDir);
      args.addAll(List.of("--symboltable", symbolOutputDir));
    }

    var outputDirectory = getOutputDir().get().toString();
    Log.debug("Output directory: " + outputDirectory);
    args.addAll(List.of("--output", outputDirectory));
    return args;
  }

  @Override
  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    var input = getInput();
    if (!input.isEmpty()) {
      result.addAll(List.of("-i", input.getAsPath()));
    }

    var symbolInputDir = getSymbolDir();
    if (!symbolInputDir.isEmpty()) {
      for (var symDir : symbolInputDir) {
        result.addAll(List.of("-path", symDir.getAbsolutePath()));
      }
    }

    return result;
  }
}
