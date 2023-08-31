/* (c) https://github.com/MontiCore/monticore */

import { SehubManager } from '@src/sehub/SehubManager';
import { GemList } from '@umlp/common';
import { GemNetworkData } from '@monticore/montigem-charts';
import { User } from '@src/sehub/User';
import { TableColumn } from '@monticore/montigem-table';
import { SehubMainPageComponentTOP } from '@src/gui/SehubMainPageComponentTOP';
import { Project } from '@src/sehub/Project';

import { Component } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";

@Component({
  selector: 'sehub-main-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/SehubMainPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export  class SehubMainPageComponent extends SehubMainPageComponentTOP {
  ngOnInit(){
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/Login/LoginPage']);
    }
    super.ngOnInit();
  }
}
