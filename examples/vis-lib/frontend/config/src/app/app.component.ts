import { Component } from '@angular/core';
import { DomainManagerClientImpl } from '@src/domain/DomainManagerClientImpl';
// JSweet
// import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/commonj2ts';
// import { DomainComponentObserverManager } from '@src/DomainComponentObserverManager'
// Non-JSweet
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    DomainManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss" : "ws";
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
      ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
    (<any>window).websocket_connected = true;
    // JSweet
    // DomainComponentObserverManager.init();
  }
}
