/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific;

import de.montigem.gradle.common.MGAllFilesTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import umlp.UMLPToolOptions;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@CacheableTask
public abstract class GUI2SymTask extends MGAllFilesTask {

  public GUI2SymTask() {
    super("GUI2SymTask");
  }

  @InputFiles
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental
  public abstract DirectoryProperty getModelDir();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<String> getTSlib();

  @Override
  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    result.add("-" + UMLPToolOptions.GEN_SYMBOLS_ONLY.getCMD());

    if (this.getOutputDir().isPresent()) {
      result.add("-" + UMLPToolOptions.SYMTAB.getCMD());
      result.add(handlePath.apply(getOutputDir().getAsFile().get().toPath()));
    }

    if (this.getModelDir().isPresent()) {
      result.add("-" + UMLPToolOptions.MODEl_PATH.getCMD());
      result.add(handlePath.apply(getModelDir().getAsFile().get().toPath()));
    }

    if (!this.getSymbolPath().isEmpty()) {
      for (File file : this.getSymbolPath()) {
        result.add("-" + UMLPToolOptions.PATH.getCMD());
        result.add(handlePath.apply(file.toPath()));
      }
    }

    if (this.getTSlib().isPresent()) {
      result.add("-" + UMLPToolOptions.GUI_GEN_FOR_TS_LIB.getCMD());
      result.add(getTSlib().get());
    }

    result.add("-" +UMLPToolOptions.GEN_IMPORT_MAP.getCMD());

    return result;
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".gui");
  }

  @Override
  protected Stream<FileChange> getStreamOfChanges(InputChanges inputChanges) {
    Stream<FileChange> result = super.getStreamOfChanges(inputChanges);
    result = Stream.concat(result, StreamSupport.stream(inputChanges.getFileChanges(getModelDir()).spliterator(), false));
    return result;
  }
}
