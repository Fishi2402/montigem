/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { AssociationsManagerClientImpl } from 'associations/AssociationsManagerClientImpl';
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/commonj2ts';
import { AssociationsComponentObserverManager } from 'AssociationsComponentObserverManager'


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    AssociationsManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss" : "ws";
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
        ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
    (<any>window).websocket_connected = true;
    // JSweet
    AssociationsComponentObserverManager.init();
  }


}
