package gradle.specific;

import de.monticore.io.FileReaderWriter;
import gradle.common.UMLPAllFilesTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import umlp.UMLPToolOptions;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public abstract class UMLPGuiGenTask extends UMLPAllFilesTask {

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  // @Incremental I am not sure if this _should_ be incremental?
  public abstract DirectoryProperty getModelDir();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract DirectoryProperty getSymbolDir();

  @OutputDirectory
  @org.gradle.api.tasks.Optional
  public abstract DirectoryProperty getSymbolOutDir();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract ListProperty<String> getDomainModels();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract ListProperty<String> getGuiCreateModels();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<File> getGuiCreateModelsAsFile();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getGuiGenTSFuncionality();


  /**
   *
   * @param f file which should be checked
   * @return if file should be used as input for UMLPTool
   */
  @Override
  public boolean isInputFile(File f){
    return f.getName().endsWith(".gui");
  }

  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    if(this.getModelDir().isPresent()) {
      result.add("-mp");
      result.add(handlePath.apply(getModelDir().getAsFile().get().toPath()));
    }

    if(this.getSymbolDir().isPresent()) {
      result.add("-path");
      result.add(handlePath.apply(getSymbolDir().getAsFile().get().toPath()));
    }

    if (this.getDomainModels().isPresent() && !this.getDomainModels().get().isEmpty()) {
      result.add("-" + UMLPToolOptions.DOMAIN.getCMD());
      result.addAll(this.getDomainModels().get());
    }

    if (this.getGuiCreateModels().isPresent() && !this.getGuiCreateModels().get().isEmpty()) {
      result.add("-" + UMLPToolOptions.GUI_CREATE.getCMD());
      result.addAll(this.getGuiCreateModels().get());
    }

    //Do not put arguments in between here!

    if(this.getGuiCreateModelsAsFile().isPresent())
    {
      if(!this.getGuiCreateModelsAsFile().get().exists())
        throw new IllegalArgumentException("File of argument guiCreateModelsAsFile does not exist");

      List<String> createModels = List.of(
          FileReaderWriter.readFromFile(this.getGuiCreateModelsAsFile().get().toPath()).split("\n")
      );
      if(!getGuiCreateModels().isPresent() || (this.getGuiCreateModels().isPresent() && this.getGuiCreateModels().get().isEmpty())) {
        result.add("-" + UMLPToolOptions.GUI_CREATE.getCMD());
      }
      result.addAll(createModels);
    }

    if (this.getGuiGenTSFuncionality().isPresent()) {
      result.add("-" + UMLPToolOptions.GUI_GEN_TS_FUNC.getCMD());
      result.add(this.getGuiGenTSFuncionality().get() + "");
    }

    return result;
  }
}
