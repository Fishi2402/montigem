/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {
  ClientCommandManager,
  ClientCommandWebsocketCommunication,
  CommandManager
} from '@umlp/common';
import {DtcockpitManagerClientImpl} from 'dtcockpit/DtcockpitManagerClientImpl';
import {LoginModule4DtcockpitManagerClientImpl} from "loginmodule/LoginModule4DtcockpitManagerClientImpl";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    // TODO: The delegate mus be first
    LoginModule4DtcockpitManagerClientImpl.init(); // TODO: Include me within DtcockpitManagerClientImpl

    DtcockpitManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss": "ws";
    // TODO: ClientCommandManager can fail async, but we need to check if the websocket is working
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
      ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
  }


}
