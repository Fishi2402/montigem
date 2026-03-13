package de.montigem.gradle.specific;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import umlp.UMLPToolOptions;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

@CacheableTask
public abstract class UMLP2TestInfrastructureTask extends UMLP2JavaTask {

  private static final String CT_PATH = "umlp.UMLP2JavaTestInfrastructure";

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getUsePlaywright();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getGenerateMonitor();

  public UMLP2TestInfrastructureTask() {
    super("UMLP2TestInfrastructureTask");

    getUsePlaywright().convention(false);
    getGenerateMonitor().convention(false);
  }

  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);

    result.add("-" + UMLPToolOptions.CFGTMPL.getCMD());
    result.add(CT_PATH);

    if (getUsePlaywright().get()) {
      result.add("-" + UMLPToolOptions.USE_PLAYWRIGHT.getCMD());
    }

    if(getGenerateMonitor().get()){
      result.add("-" + UMLPToolOptions.GEN_MONITOR.getCMD());
    }

    return result;
  }
}
