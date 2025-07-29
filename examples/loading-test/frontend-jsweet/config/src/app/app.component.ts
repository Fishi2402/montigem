import { Component } from '@angular/core';
import { LoadingTestsManagerClientImpl } from 'loadingtests/LoadingTestsManagerClientImpl';
// JSweet
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/commonj2ts';
import { LoadingTestsComponentObserverManager } from 'LoadingTestsComponentObserverManager'
import { LoadingTestsManager } from 'loadingtests/LoadingTestsManager';

declare global {
  interface Window {
    LoadingTestsManager: typeof import('loadingtests/LoadingTestsManager').LoadingTestsManager
  }
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    LoadingTestsManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss" : "ws";
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
      ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
    (<any>window).LoadingTestsManager = LoadingTestsManager;
    (<any>window).websocket_connected = true;
    // JSweet
    LoadingTestsComponentObserverManager.init();
  }
}
