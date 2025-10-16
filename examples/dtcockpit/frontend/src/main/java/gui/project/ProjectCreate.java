/* (c) https://github.com/MontiCore/monticore */
package gui.project;

import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ProjectCreate extends ProjectCreateTOP {
  private String error = "";

  @Override
  public Consumer<Void> createProject() {
    return $ -> {
      this.getProject().build();
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
