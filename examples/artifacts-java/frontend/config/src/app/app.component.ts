import { Component } from '@angular/core';
import { JavaProjectManagerClientImpl } from 'javaproject/JavaProjectManagerClientImpl';
// JSweet
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/commonj2ts';
import { JavaProjectComponentObserverManager } from 'JavaProjectComponentObserverManager'
// Non-JSweet

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    JavaProjectManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss" : "ws";
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
        ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
    (<any>window).websocket_connected = true;
    // JSweet
    JavaProjectComponentObserverManager.init();
  }
}
