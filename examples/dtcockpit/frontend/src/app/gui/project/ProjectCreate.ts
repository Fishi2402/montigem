/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {DtcockpitManager} from 'dtcockpit/DtcockpitManager';
import {ProjectCreateComponent,config} from 'gui/project/ProjectCreateComponent';
import {ProjectErrorCollector} from 'dtcockpit/ProjectErrorCollector';
import {Project} from 'dtcockpit/Project';


@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ProjectCreate extends ProjectCreateComponent {

  private error: string = "";

  public createProject(): (arg: void) => void {
    return $ => {
      if (!this.checkBuild()) return;
      DtcockpitManager.projectBuilder().then(gemPromiseRes => {
        this.project.build();
        this.project = gemPromiseRes;
      });
    }
  }

  protected checkBuild(): boolean {
    // Listen to project errors and set the error text accordingly
    const observer = {
      notifyNameInvalid: (project: Project,msg: string)  : void =>  {
        this.error = msg;
      },
      notifyGitInvalid: (project: Project,msg: string): void => {
        this.error = msg;
      },
      notifyPStatisticsInvalid: (project: Project,msg: string): void => {
        this.error = msg;
      },
      notifyLastPipelineInvalid: (project: Project,msg: string): void => {
        this.error = msg;
      },
      notifyBranchesInvalid: (project: Project,msg: string): void => {
        this.error = msg;
      },
      notifyDependentProjectsInvalid: (project: Project,msg: string): void => {
        this.error = msg;
      },
      notifyCiInfosInvalid: (project: Project,msg: string): void => {
        this.error = msg;
      },
      notifyCmdInvalid: (project: Project,msg: string): void => {
        this.error = msg;
      }
    };
    ProjectErrorCollector.addErrorObserver(observer);
    if (!this.project.isBuildable()) {
      ProjectErrorCollector.removeErrorObserver(observer);
      return false;
    }
    ProjectErrorCollector.removeErrorObserver(observer);
    return true;
  }

  public override getErrorValue(): string {
    return this.error;
  }

  public override navigateBack(): (arg: void) => void {
    return $ => this._router.navigate(['gui', 'projects']);
  }
}
