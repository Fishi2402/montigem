/* (c) https://github.com/MontiCore/monticore */

import { GemNetworkData } from '@umlp/charts';
import { SehubMainPageTOP } from '@src/gui/SehubMainPageTOP';

import { Directive } from '@angular/core';


@Directive()

export  class SehubMainPage extends SehubMainPageTOP {

  public  getGemNetworkGraph1Data (): GemNetworkData  {
    return null;
  }

  public override goToProjectPage (project: any): (arg: any) => void {
    return event => this.router.navigate(["gui", "ProjectFileView", project.getRootArtifact()[0].getGemId()]);
  }


}


