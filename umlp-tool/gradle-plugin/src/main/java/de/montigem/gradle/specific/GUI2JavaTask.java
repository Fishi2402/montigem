/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle.specific;

import de.monticore.io.FileReaderWriter;
import de.montigem.gradle.common.MGAllFilesTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import umlp.UMLPToolOptions;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

// Task might not be a @CacheableTask since relative paths are generated for HWC-files
public abstract class GUI2JavaTask extends MGAllFilesTask {

  public GUI2JavaTask() {
    super("GUI2JavaTask");
  }

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  // @Incremental I am not sure if this _should_ be incremental?
  public abstract DirectoryProperty getModelDir();

  @InputFiles
  public abstract ConfigurableFileCollection getImportsDir();

  @OutputDirectory
  @org.gradle.api.tasks.Optional
  public abstract DirectoryProperty getSymbolOutDir();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<String> getDomain();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract ListProperty<String> getGuiCreateModels();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<File> getGuiCreateModelsAsFile();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getGUIGenTSFunctionality();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getGUIGenJSweet();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<String> getTSlib();

  /**
   * @param f file which should be checked
   * @return if file should be used as input for UMLPTool
   */
  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".gui");
  }

  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    if (this.getModelDir().isPresent()) {
      result.add("-mp");
      result.add(handlePath.apply(getModelDir().getAsFile().get().toPath()));
    }

    if (this.getSymbolOutDir().isPresent()) {
      result.add("-s");
      result.add(handlePath.apply(getSymbolOutDir().getAsFile().get().toPath()));
    }

    for (var symDir : this.getSymbolPath()) {
      result.add("-path");
      result.add(symDir.getAbsolutePath());
    }

    for (var importDir : this.getImportsDir()) {
      result.add("-" + UMLPToolOptions.IMPORTS_PATH.getCMD());
      result.add(importDir.getAbsolutePath());
    }

    if (this.getDomain().isPresent() && !this.getDomain().get().isEmpty()) {
      result.add("-" + UMLPToolOptions.DOMAIN.getCMD());
      result.add(this.getDomain().get());
    }

    if (this.getGuiCreateModels().isPresent() && !this.getGuiCreateModels().get().isEmpty()) {
      result.add("-" + UMLPToolOptions.GUI_CREATE.getCMD());
      result.addAll(this.getGuiCreateModels().get());
    }

    //Do not put arguments in between here!

    if (this.getGuiCreateModelsAsFile().isPresent()) {
      if (!this.getGuiCreateModelsAsFile().get().exists())
        throw new IllegalArgumentException("File of argument guiCreateModelsAsFile does not exist");

      List<String> createModels = List.of(
          FileReaderWriter.readFromFile(this.getGuiCreateModelsAsFile().get().toPath()).split("\n")
      );
      if (!getGuiCreateModels().isPresent() || (this.getGuiCreateModels().isPresent() && this.getGuiCreateModels().get().isEmpty())) {
        result.add("-" + UMLPToolOptions.GUI_CREATE.getCMD());
      }
      result.addAll(createModels);
    }

    if (this.getGUIGenTSFunctionality().isPresent()) {
      result.add("-" + UMLPToolOptions.GUI_GEN_TS_FUNC.getCMD());
      result.add(this.getGUIGenTSFunctionality().get() + "");
    }

    if (this.getGUIGenJSweet().isPresent()) {
      result.add("-" + UMLPToolOptions.GUI_GEN_JSWEET.getCMD());
      result.add(this.getGUIGenJSweet().get() + "");
    }

    if (this.getTSlib().isPresent()) {
      result.add("-" + UMLPToolOptions.GUI_GEN_FOR_TS_LIB.getCMD());
      result.add(getTSlib().get());
    }

    return result;
  }
}
