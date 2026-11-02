import { Component } from '@angular/core';
import { CoverageDashboardManagerClientImpl } from 'coveragedashboard/CoverageDashboardManagerClientImpl';
import { CoverageDashboardComponentObserverManager } from 'CoverageDashboardComponentObserverManager';
import {GemLineChartTypesDeSer} from '@umlp/charts'
import { ClientCommandManager,BaseClassObjectMapper , ClientCommandWebsocketCommunication, CommandManager } from '@umlp/commonj2ts';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    CoverageDashboardManagerClientImpl.init();
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
      `${document.baseURI.replace(/^http/, 'ws').replace(/\/$/, '')}` + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
    (<any>window).websocket_connected = true;
    CoverageDashboardComponentObserverManager.init();
    BaseClassObjectMapper.addDeSers(GemLineChartTypesDeSer.getInstance().getDeSerMap())
  }
}
