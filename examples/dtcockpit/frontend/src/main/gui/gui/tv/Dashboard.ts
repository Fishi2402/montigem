/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { DashboardComponent,config } from 'gui/tv/DashboardComponent';
import { App } from 'dtcockpit/App';
import { Meeting } from 'dtcockpit/Meeting';
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

