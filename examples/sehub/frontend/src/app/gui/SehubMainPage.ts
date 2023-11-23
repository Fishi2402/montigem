/* (c) https://github.com/MontiCore/monticore */

import {GemNetworkData} from '@umlp/charts';
import {SehubMainPageComponent} from '@src/gui/SehubMainPageComponent';

import {Component} from '@angular/core';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";

@Component({
  selector: 'sehub-main-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/SehubMainPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class SehubMainPage extends SehubMainPageComponent {

  ngOnInit() {
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['gui/login/LoginPage']);
    }
    super.ngOnInit();
  }

  public  getGemNetworkGraph1Data (): GemNetworkData  {
    return null;
  }

  public override goToProjectPage (project: any): (arg: any) => void {
    return event => this.router.navigate([ "gui", "ProjectFileView", project.getRootArtifact()[0].getGemId()]);
  }



}


