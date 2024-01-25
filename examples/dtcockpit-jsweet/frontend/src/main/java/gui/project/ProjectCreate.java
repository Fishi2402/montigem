/* (c) https://github.com/MontiCore/monticore */
package gui.project;

import dtcockpit.DtcockpitManager;
import dtcockpit.ProjectBuilder;
import dtcockpit.ProjectErrorCollector;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

@Component
public class ProjectCreate extends ProjectCreateTOP {
  private String error = "";

  @Override
  public void createProject(Object event) {
    if (!this.checkBuild()) return;
    ProjectBuilder projectBuilder = DtcockpitManager.projectBuilder();
    this.build();
    this.project = projectBuilder;
  }

  protected boolean checkBuild() {
//    // Listen to project errors and set the error text accordingly
//    const observer = {
//        notifyNameInvalid: (project: Project,msg: string)  : void =>  {
//      this.error = msg;
//    },
//    notifyGitInvalid: (project: Project,msg: string): void => {
//      this.error = msg;
//    },
//    notifyPStatisticsInvalid: (project: Project,msg: string): void => {
//      this.error = msg;
//    }
//    };
//    ProjectErrorCollector.addErrorObserver(observer);
//    if (!this.projectBuilder.isBuildable()) {
//      ProjectErrorCollector.removeErrorObserver(observer);
//      return false;
//    }
//    ProjectErrorCollector.removeErrorObserver(observer);
    return true;
  }

  public String getErrorValue() {
    return this.error;
  }

  @Override
  public void navigateBack(Object event) {
    Router.navigate("gui", "projects");
  }
}
