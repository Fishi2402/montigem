import { Component } from '@angular/core';
import { CarrentalManagerClientImpl } from '@src/carrental/CarrentalManagerClientImpl';
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/commonj2ts';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    CarrentalManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss": "ws";
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
      ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
  }
}
