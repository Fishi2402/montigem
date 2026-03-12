package de.montigem.gradle.specific.cd;

import org.gradle.api.provider.Property;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;

public abstract class CDGeneratorAction implements WorkAction<CDGeneratorAction.CDGenParams> {

  @Override
  public void execute() {
    new de.monticore.CDGeneratorTool().run(getParameters().getArgs().get());
  }

  public interface CDGenParams extends WorkParameters {

    Property<String[]> getArgs();
  }
}
