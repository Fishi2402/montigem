/* (c) https://github.com/MontiCore/monticore */

import {GemNetworkData} from '@umlp/charts';
import {SehubMainPageComponent,config} from '@src/gui/SehubMainPageComponent';

import {Component} from '@angular/core';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import {Project} from '@src/sehub/Project';
import { Event } from '@umlp/common';

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

  public override goToProjectPage(project: Project): Event {
    return () => this._router.navigate([ "gui", "ProjectFileView", project.getRootArtifact1(0).getGemId()]);
  }



}


