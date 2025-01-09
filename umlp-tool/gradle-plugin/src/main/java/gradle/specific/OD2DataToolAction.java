package gradle.specific;

import de.monticore.od4development.OD4DevelopmentTool;
import org.gradle.api.provider.Property;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;

public abstract class OD2DataToolAction implements WorkAction<OD2DataToolAction.Params> {

  @Override
  public void execute() {
    OD4DevelopmentTool.main(getParameters().getArgs().get());
  }

  public interface Params extends WorkParameters {
    Property<String[]> getArgs();
  }
}
