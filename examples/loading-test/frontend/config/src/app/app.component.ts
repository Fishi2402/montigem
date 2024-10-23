import { Component } from '@angular/core';
import { LoadingTestsManagerClientImpl } from '@src/loadingtests/LoadingTestsManagerClientImpl';
import { LoadingTestsManager } from '@src/loadingtests/LoadingTestsManager';
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/common';

declare global {
  interface Window {
    LoadingTestsManager: typeof import('@src/loadingtests/LoadingTestsManager').LoadingTestsManager
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
    (<any>window).websocket_connected = true;
    (<any>window).LoadingTestsManager = LoadingTestsManager
  }
}
