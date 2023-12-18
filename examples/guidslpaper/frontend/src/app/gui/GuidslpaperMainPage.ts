/* (c) https://github.com/MontiCore/monticore */

import {GuidslpaperMainPageComponent} from '@src/gui/GuidslpaperMainPageComponent';
import {Component} from '@angular/core';
import {Person} from '@src/guidslpaper/Person';
import {GuidslpaperSecurityManager} from "../guidslpaper/GuidslpaperSecurityManager";

@Component({
  selector: 'guidslpaper-main-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/GuidslpaperMainPage.html',
  styles: [":host{ display: flex; flex-direction: row }"],
  styleUrls: []
})

export class GuidslpaperMainPage extends GuidslpaperMainPageComponent {
  ngOnInit(){
    if (!GuidslpaperSecurityManager.isSignedIn()) {
      this.router.navigate(['gui', 'login', 'LoginPage']);
    }
    super.ngOnInit();
  }


  public navigateToPerson(p: Person): (arg: any) => void {
    this.router.navigate(['gui', 'person', p.getKuerzel(), 'show'])
    return () => { }
  }

  public navigateToNewPerson(event: any): void {
    this.router.navigate(['gui', 'person', 'PersonCreate'])
  }


  public getPButtonClasses(p: Person): Array<string> { // ALu: Workaround - discuss
    return ["person-dashboard-btn"];
  }

  public getNewButtonClasses(): Array<string> { // ALu: Workaround - discuss
    return ["person-dashboard-btn"];
  }
}
