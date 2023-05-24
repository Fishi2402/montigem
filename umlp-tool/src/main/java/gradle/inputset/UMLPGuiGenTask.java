package gradle.inputset;

import de.se_rwth.commons.logging.Log;
import gradle.CommonUMLPTask;
import gradle.UMLPAction;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.IgnoreEmptyDirectories;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import umlp.UMLPTool;
import umlp.UMLPToolOptions;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class UMLPGuiGenTask extends CommonUMLPTask {

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental
  public abstract DirectoryProperty getModelDir();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental
  public abstract DirectoryProperty getSymbolDir();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract ListProperty<String> getDomainModels();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract ListProperty<String> getGuiCreateModels();


  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    result.add("-r");
    result.add(handlePath.apply(getReportDir().getAsFile().get().toPath()));

    this.getInput().getAsFileTree().getFiles().stream()
        .filter(File::isFile)
        .filter(f -> f.getName().endsWith(".gui"))
        .forEach(f -> {
          result.add("-i");
          result.add(handlePath.apply(f.toPath()));
        });

    if(this.getModelDir().isPresent()) {
      result.add("-path");
      result.add(handlePath.apply(getModelDir().getAsFile().get().toPath()));
      if (this.getSymbolDir().isPresent()) {
        result.add(handlePath.apply(getSymbolDir().getAsFile().get().toPath()));
      }
    }

    if (this.getDomainModels().isPresent() && !this.getDomainModels().get().isEmpty()) {
      result.add("-" + UMLPToolOptions.DOMAIN.getCMD());
      result.addAll(this.getDomainModels().get());
    }

    if (this.getGuiCreateModels().isPresent() && !this.getGuiCreateModels().get().isEmpty()) {
      result.add("-" + UMLPToolOptions.GUI_CREATE.getCMD());
      result.addAll(this.getGuiCreateModels().get());
    }

    return result;
  }

  private void startGeneration() {
    Path cwd = getProject().getProjectDir().toPath().toAbsolutePath();
    Log.warn("Starting UMLPTool: \n" +
        "\t  java -jar UMLPTool.jar " +
        String.join(" ", createArgList(p -> "." + File.separator + cwd.relativize(p)))
    );

    List<String> args = createArgList(p -> p.toAbsolutePath().toString());
    super.startGeneration(args);
  }

  @TaskAction
  public void run(InputChanges inputChanges) {
    Log.warn("This Task is still in development and experimental, no incremental logic exists!");

    workQueue = getWorkerExecutor().classLoaderIsolation();
    startGeneration();
  }
}
