/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { DashboardComponent } from '@src/gui/tv/DashboardComponent';
import { App } from '@src/dtcockpit/App';
import { Meeting } from '@src/dtcockpit/Meeting';
import { GemList } from '@umlp/common';
import {config} from "@src/gui/tv/DashboardStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'dashboard',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/tv/Dashboard.html',
  styleUrls: ['./Dashboard.scss',]
})
export class Dashboard extends DashboardComponent {

  public override getCalendarMeetings(app: App): GemList<Meeting> {
    return app._internal_getDisplayedMeetingsGemList();
  }

}

