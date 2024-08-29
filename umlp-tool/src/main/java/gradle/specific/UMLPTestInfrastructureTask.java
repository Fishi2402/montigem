package gradle.specific;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
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
