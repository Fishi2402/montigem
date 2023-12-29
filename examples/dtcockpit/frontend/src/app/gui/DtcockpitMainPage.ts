/* (c) https://github.com/MontiCore/monticore */

import {DtcockpitMainPageComponent,config} from '@src/gui/DtcockpitMainPageComponent';
import {Component} from '@angular/core';
import {Project} from '@src/dtcockpit/Project';
import {DtcockpitSecurityManager} from "../dtcockpit/DtcockpitSecurityManager";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class DtcockpitMainPage extends DtcockpitMainPageComponent {
  ngOnInit(){
    if (!DtcockpitSecurityManager.isSignedIn()) {
      this.router.navigate(['gui', 'login', 'LoginPage']);
    }
    super.ngOnInit();
  }


  public navigateToProject(p: Project): (arg: any) => void {
    this.router.navigate(['gui', 'project', p.getName(), 'show'])
    return () => { }
  }

  public navigateToNewProject(event: any): void {
    this.router.navigate(['gui', 'project', 'ProjectCreate'])
  }


  public getPButtonClasses(p: Project): Array<string> { // ALu: Workaround - discuss
    return ["project-dashboard-btn"];
  }

  public getNewButtonClasses(): Array<string> { // ALu: Workaround - discuss
    return ["project-dashboard-btn"];
  }
}
