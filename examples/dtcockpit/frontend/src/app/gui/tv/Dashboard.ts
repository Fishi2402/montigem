/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { DashboardComponent,config } from '@src/gui/tv/DashboardComponent';
import { App } from '@src/dtcockpit/App';
import { Meeting } from '@src/dtcockpit/Meeting';
import { GemList } from '@umlp/common';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class Dashboard extends DashboardComponent {

}

