package de.montigem.gradle.specific;

import de.monticore.gradle.common.AToolAction;
import de.montigem.gradle.common.MGAllFilesTask;
import de.montigem.gradle.common.ODs2DataAction;
import de.montigem.gradle.common.ODs2DataToolInvoker;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ODs2DataTask extends MGAllFilesTask {

  @OutputDirectory
  @Optional
  public abstract DirectoryProperty getSymbolOutDir();

  public ODs2DataTask() {
    super("ODs2DataTask");
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".od");
  }

  @Override
  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = new ArrayList<>();

    for (File f : getInputFilesAsStream().collect(Collectors.toList())) {
      result.add("-i");
      result.add(handlePath.apply(f.toPath()));
    }

    if (!this.getSymbolPath().isEmpty()) {
      for (var symDir : this.getSymbolPath()) {
        result.add("-path");
        result.add(symDir.getAbsolutePath());
      }
    }

    result.add("-o");
    result.add(handlePath.apply(getOutputDir().get().getAsFile().toPath()));

    if (getSymbolOutDir().isPresent()) {
      result.add("-s");
      result.add(handlePath.apply(getSymbolOutDir().get().getAsFile().toPath()));
    }

    return result;
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return ODs2DataAction.class;
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return ODs2DataToolInvoker::run;
  }
}
