/* (c) https://github.com/MontiCore/monticore */

import { SehubManager } from '@src/sehub/SehubManager';
import { GemList } from '@umlp/common';
import { GemNetworkData } from '@monticore/montigem-charts';
import { User } from '@src/sehub/User';
import { TableColumn } from '@monticore/montigem-table';
import { SehubMainPageTOP } from '@src/gui/SehubMainPageTOP';
import { Project } from '@src/sehub/Project';

import { Component } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'sehub-main-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/SehubMainPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export  class SehubMainPage extends SehubMainPageTOP {

  public  getGemNetworkGraph1Data (): GemNetworkData  {
    return null;
  }

  public override navigateToProjectPage(row: Project): void {
    this.router.navigate(["..", "project", "ProjectFileView", row.getRootArtifact()[0].getGemId()]);
  }

}


