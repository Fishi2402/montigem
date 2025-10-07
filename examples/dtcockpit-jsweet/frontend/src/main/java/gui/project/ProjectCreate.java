/* (c) https://github.com/MontiCore/monticore */
package gui.project;

import dtcockpit.DtcockpitManager;
import dtcockpit.ProjectBuilder;
import dtcockpit.ProjectErrorCollector;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ProjectCreate extends ProjectCreateTOP {
  private String error = "";

  @Override
  public Consumer<Void> createProject() {
    return $ -> {
      if (!this.checkBuild()) return;
      this.getProject().build();
    };
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
  public Consumer<Void> navigateBack() {
    return $ -> Router.navigate("gui", "projects");
  }
}
