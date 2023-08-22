import { Component } from '@angular/core';
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/common';
import {SehubManagerClientImpl} from '@src/sehub/SehubManagerClientImpl';
import { Location } from '@angular/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  private currentUrl : string;
  constructor(public location: Location) {
    this.currentUrl = this.location.path();
    SehubManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss": "ws";
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
      ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
  }
}
