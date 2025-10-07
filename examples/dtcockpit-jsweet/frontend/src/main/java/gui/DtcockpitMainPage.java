/* (c) https://github.com/MontiCore/monticore */
package gui;

import dtcockpit.DtcockpitSecurityManager;
import dtcockpit.Project;
import umlp.backendrte.common.GemList;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class DtcockpitMainPage extends DtcockpitMainPageTOP {

  @Override
  public void init() {
    if (!DtcockpitSecurityManager.isSignedIn()) {
      Router.navigate("gui", "login", "LoginPage");
    }
    super.init();
  }

  @Override
  public Consumer<Void> navigateToProject(Project p) {
// TODO   Router.navigate("gui", "project", p.getName(), "show");
    return e -> { };
  }

  @Override
  public Consumer<Void> navigateToNewProject() {
    return $ -> Router.navigate("gui", "project", "ProjectCreate");
  }

  @Override
  public String getPButtonLabel(Project p) {
    return "project-dashboard-btn";
  }

  @Override
  public String getNewButtonLabel() {
    return "project-dashboard-btn";
  }
}
