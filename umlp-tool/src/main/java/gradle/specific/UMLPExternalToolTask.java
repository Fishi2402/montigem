package gradle.specific;

import gradle.common.UMLPSingleFileTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;

import java.io.File;
import java.nio.file.Path;
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
