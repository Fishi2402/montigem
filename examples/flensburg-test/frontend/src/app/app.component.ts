import { Component } from '@angular/core';
import { FlensburgManagerClientImpl } from '@src/hardy/flensburg/FlensburgManagerClientImpl';
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    FlensburgManagerClientImpl.init();
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
      'ws://localhost:4200/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
  }
}
