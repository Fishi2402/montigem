/* (c) https://github.com/MontiCore/monticore */
package gui.project;

import dtcockpit.DtcockpitManager;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ProjectsCreateByGroup extends ProjectsCreateByGroupTOP {
  private String error = "";

  @Override
  public Consumer<Void> createGroup() {
    return $ -> {
      getGroup().build();
      setGroup(DtcockpitManager.groupBuilder());
    };
  }

  public String getErrorValue() {
    return this.error;
  }

  @Override
  public Consumer<Void> navigateBack() {
    return $ -> Router.navigate("gui", "projects");
  }
}
