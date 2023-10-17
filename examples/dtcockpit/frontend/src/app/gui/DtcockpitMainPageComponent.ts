/* (c) https://github.com/MontiCore/monticore */

import {DtcockpitMainPageComponentTOP} from '@src/gui/DtcockpitMainPageComponentTOP';

import {Component} from '@angular/core';
import {DtcockpitSecurityManager} from "../dtcockpit/DtcockpitSecurityManager";
import { Project } from '@src/dtcockpit/Project';

@Component({
  selector: 'dtcockpit-main-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/DtcockpitMainPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class DtcockpitMainPageComponent extends DtcockpitMainPageComponentTOP {
  ngOnInit(){
    if (!DtcockpitSecurityManager.isSignedIn()) {
      this.router.navigate(['/login/LoginPage']);
    }
    super.ngOnInit();
  }


  public navigateToProject(p: Project): (arg: any) => void {
    this.router.navigate(['/project/', p.getName(), 'show'])
    return () => {
    }
  }

  public navigateToNewProject(event: any): void {
    this.router.navigate(['/project/new'])
  }


  public getPButtonClasses(p: Project): Array<string> { // ALu: Workaround - discuss
    return ["project-dashboard-btn"];
  }

  public getNewButtonClasses(): Array<string> { // ALu: Workaround - discuss
    return ["project-dashboard-btn"];
  }
}
