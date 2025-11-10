/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/commonj2ts';
import {SehubManagerClientImpl} from 'sehub/SehubManagerClientImpl';
import {Navigation} from "navigation/navigation.component";
import {SehubComponentObserverManager} from 'SehubComponentObserverManager';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  private currentUrl : string;
  constructor() {
    SehubManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss": "ws";
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
      ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
    SehubComponentObserverManager.init();
  }
}
