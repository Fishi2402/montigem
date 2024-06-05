/* (c) https://github.com/MontiCore/monticore */

import {GuidslpaperMainPageComponent,config} from '@src/gui/GuidslpaperMainPageComponent';
import {Component} from '@angular/core';
import {Person} from '@src/guidslpaper/Person';
import {GuidslpaperSecurityManager} from "../guidslpaper/GuidslpaperSecurityManager";

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class GuidslpaperMainPage extends GuidslpaperMainPageComponent {
  ngOnInit(){
    if (!GuidslpaperSecurityManager.isSignedIn()) {
      this._router.navigate(['gui', 'login', 'LoginPage']);
    }
    super.ngOnInit();
  }

  public navigateToPerson(p: Person): (arg: any) => void {
    return $ => this._router.navigate(['gui', 'person', p.getKuerzel(), 'show']);
  }

  public navigateToNewPerson(): (arg: void) => void {
    return $ => this._router.navigate(['gui', 'person', 'PersonCreate']);
  }

  public getPButtonClasses(p: Person): Array<string> { // ALu: Workaround - discuss
    return ["person-dashboard-btn"];
  }

  public getNewButtonClasses(): Array<string> { // ALu: Workaround - discuss
    return ["person-dashboard-btn"];
  }
}
