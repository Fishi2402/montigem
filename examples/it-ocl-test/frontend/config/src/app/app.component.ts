/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { MydomainManagerClientImpl } from 'mydomain/MydomainManagerClientImpl';
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/commonj2ts';
import { MydomainComponentObserverManager } from 'MydomainComponentObserverManager'


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    MydomainManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss" : "ws";
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
        ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
    (<any>window).websocket_connected = true;
    // JSweet
    MydomainComponentObserverManager.init();
  }


}
