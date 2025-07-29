package gui;/* (c) https://github.com/MontiCore/monticore */

import sehub.Project;
import sehub.SehubSecurityManager;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component()
public class SehubMainPage extends SehubMainPageTOP {

  public void init() {
    if (!SehubSecurityManager.isSignedIn()) {
      Router.navigate("gui/login/LoginPage");
    }
    super.init();
  }

  public Consumer<Void> goToProjectPage(Project project) {
    return event -> Router.navigate("gui/ProjectFileView/"+project.getRootArtifact(0).getGemId());
  }
}


