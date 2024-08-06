/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {
  ClientCommandManager,
  ClientCommandWebsocketCommunication,
  CommandManager
} from '@umlp/commonj2ts';
import {JsweettestManagerClientImpl} from '@src/jsweettest/JsweettestManagerClientImpl';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    JsweettestManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss": "ws";
    // TODO: ClientCommandManager can fail async, but we need to check if the websocket is working
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
      ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
    // for playwright tests
    (<any>window).websocket_connected = true;
  }


}
