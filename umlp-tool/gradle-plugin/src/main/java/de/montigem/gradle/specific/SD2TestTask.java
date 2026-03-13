package de.montigem.gradle.specific;

import de.monticore.gradle.common.AToolAction;
import de.montigem.gradle.common.MGAllFilesTask;
import de.montigem.gradle.common.SD2TestAction;
import de.montigem.gradle.common.SD2TestToolInvoker;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class SD2TestTask extends MGAllFilesTask {

  @InputFile
  public abstract RegularFileProperty getDomainModel();

  @org.gradle.api.tasks.InputFiles
  public abstract org.gradle.api.file.ConfigurableFileCollection getTestSymbolDirectory();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  public abstract DirectoryProperty getHwcDirectory();

  public SD2TestTask() {
    super("SD2TestTask");
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".sd");
  }

  @Override
  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = new ArrayList<>();

    result.add("--domainModel");
    result.add(handlePath.apply(getDomainModel().get().getAsFile().toPath()));

    for (File f : getInputFilesAsStream().collect(Collectors.toList())) {
      result.add("--model");
      result.add(handlePath.apply(f.toPath()));
    }

    for (File f : getTestSymbolDirectory().getFiles()) {
      result.add("--testSymbolDirectory");
      result.add(handlePath.apply(f.toPath()));
    }

    result.add("--outputDirectory");
    result.add(handlePath.apply(getOutputDir().get().getAsFile().toPath()));

    if (getHwcDirectory().isPresent()) {
      result.add("--hwcDirectory");
      result.add(handlePath.apply(getHwcDirectory().get().getAsFile().toPath()));
    }

    return result;
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return SD2TestAction.class;
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return SD2TestToolInvoker::run;
  }
}
