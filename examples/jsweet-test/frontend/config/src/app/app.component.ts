import { Component } from '@angular/core';
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from 'umlp-frontend-rte';
import {JsweettestManagerClientImpl} from '@src/jsweettest/JsweettestManagerClientImpl';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  private currentUrl : string;
  constructor() {
    JsweettestManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss": "ws";
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
      ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
  }


}
