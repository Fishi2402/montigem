/* (c) https://github.com/MontiCore/monticore */
package gradle.specific.cd;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCSingleFileTask;
import de.monticore.gradle.internal.isolation.CachedIsolation;
import de.se_rwth.commons.logging.Log;
import gradle.common.UMLPAction;
import gradle.common.UMLPToolInvoker;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CD2POJOTask extends MCSingleFileTask {

  public CD2POJOTask() {
    super("CD2PojoTask", null);
  }

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  // No @Incremental, changes of the Symbols lead to full rebuild
  public abstract DirectoryProperty getTemplatePath();

  @Override
  public boolean isInputFile(File f){
    return f.getName().endsWith(".cd");
  }

  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);

    {
      result.add("--input");
      result.add(handlePath.apply(filePath));
    }
    {
      result.add("--output");
      result.add(handlePath.apply(getOutputDir().get().getAsFile().toPath()));
    }
    if (!getHandWrittenCodeDir().isEmpty()) {
      result.add("--handwrittencode");
      result.add(handlePath.apply(getHandWrittenCodeDir().getSingleFile().toPath()));
    }
    if (getTemplatePath().isPresent()) {
      result.add("--template");
      result.add(handlePath.apply(getTemplatePath().get().getAsFile().toPath()));
    }

    result.add("-c2mc");

    return result;
  }


  @Override
  protected Consumer<String[]> getRunMethod() {
    return CDToolInvoker::run;
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return CDAction.class;
  }

  @Override
  protected void checkParameters() throws IllegalArgumentException {
    super.checkParameters();
    if(this.getDebug().get()){
      throw new IllegalArgumentException("Debugging is not allowed for this task. Further attempts will be logged.");
      // Because the CDTool does not support the "-d" flag
    }
  }
}
