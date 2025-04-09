package gradle.specific;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import umlp.UMLPToolOptions;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

@CacheableTask
public abstract class UMLPTestInfrastructureTask extends UMLPBackendTask {

  private static final String CT_PATH = "umlp.UMLP2JavaTestInfrastructure";

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getUsePlaywright();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract ConfigurableFileCollection getTestModelDir();

  public UMLPTestInfrastructureTask() {
    super("UMLPTestInfrastructureTask");

    getUsePlaywright().convention(false);
  }

  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);

    result.add("-" + UMLPToolOptions.CFGTMPL.getCMD());
    result.add(CT_PATH);

    if (getUsePlaywright().get()) {
      result.add("-" + UMLPToolOptions.USE_PLAYWRIGHT.getCMD());
    }

    return result;
  }
}
