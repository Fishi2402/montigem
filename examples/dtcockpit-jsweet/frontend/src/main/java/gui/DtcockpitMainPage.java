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
  public DtcockpitMainPage(GemList projects) {
    super(projects);
  }

  @Override
  public void init() {
    if (!DtcockpitSecurityManager.isSignedIn()) {
      Router.navigate("gui", "login", "LoginPage");
    }
  }

  @Override
  public Consumer<Object> navigateToProject(Object p) {
// TODO   Router.navigate("gui", "project", p.getName(), "show");
    return e -> { };
  }

  public void navigateToNewProject(Object event) {
    Router.navigate("gui", "project", "ProjectCreate");
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
