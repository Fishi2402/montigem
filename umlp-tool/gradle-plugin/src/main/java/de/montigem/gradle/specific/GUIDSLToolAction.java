package de.montigem.gradle.specific;

import de.monticore.guidsl.GUIDSLTool;
import org.gradle.api.provider.Property;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;

public abstract class GUIDSLToolAction implements WorkAction<GUIDSLToolAction.GUIParams> {

  @Override
  public void execute() {
    new GUIDSLTool().run(getParameters().getArgs().get());
  }

  public interface GUIParams extends WorkParameters {
    Property<String[]> getArgs();
  }
}
