/* (c) https://github.com/MontiCore/monticore */

import {GemNetworkData} from '@umlp/charts';
import {SehubMainPageComponent,config} from 'gui/SehubMainPageComponent';

import {Component} from '@angular/core';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class SehubMainPage extends SehubMainPageComponent {

  ngOnInit() {
    if (!SehubSecurityManager.isSignedIn()) {
      this._router.navigate(['gui/login/LoginPage']);
    }
    super.ngOnInit();
  }

  public  getGemNetworkGraph1Data (): GemNetworkData  {
    return null;
  }

  public override goToProjectPage (project: any): (arg: any) => void {
    return event => this._router.navigate([ "gui", "ProjectFileView", project.getRootArtifact()[0].getGemId()]);
  }



}


