/* (c) https://github.com/MontiCore/monticore */

import { SehubManager } from '@src/sehub/SehubManager';
import { GemList } from '@umlp/common';
import { GemNetworkData } from '@monticore/montigem-charts';
import { User } from '@src/sehub/User';
import { TableColumn } from '@monticore/montigem-table';
import { SehubMainPageTOP } from '@src/gui/SehubMainPageTOP';
import { Project } from '@src/sehub/Project';

import { Directive } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";

@Directive()

export  class SehubMainPage extends SehubMainPageTOP {

  public  getGemNetworkGraph1Data (): GemNetworkData  {
    return null;
  }

  public override goToProjectPage (project: any): (arg: any) => void {
    return event => this.router.navigate(["..", "project", "ProjectFileView", project.getRootArtifact()[0].getGemId()]);
  }


}


