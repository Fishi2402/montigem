/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {DtcockpitManager} from '@src/dtcockpit/DtcockpitManager';
import {ProjectCreateComponent} from '@src/gui/project/ProjectCreateComponent';
import { ProjectErrorCollector } from '@src/dtcockpit/ProjectErrorCollector';
import { Project } from '@src/dtcockpit/Project';


@Component({
  selector: 'project-create',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/project/ProjectCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class ProjectCreate extends ProjectCreateComponent {

  private error: string = "";

  public createProject(event: any): void {
    if (!this.checkBuild()) return;
    DtcockpitManager.projectBuilder().then(gemPromiseRes => {
      this.build();
      this.projectBuilder = gemPromiseRes;
    });

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
    if (!this.projectBuilder.isBuildable()) {
      ProjectErrorCollector.removeErrorObserver(observer);
      return false;
    }
    ProjectErrorCollector.removeErrorObserver(observer);
    return true;
  }

  public override getErrorValue(): string {
    return this.error;
  }

  public override navigateBack(): void {
    this.router.navigate(['gui', 'projects']);
  }
}
