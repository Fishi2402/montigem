/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { PersonCreate } from './stanley/personcreate/person-create/person-create.component';
import { PersonOverview } from './stanley/personoverview/person-overview/person-overview.component';
import { FlensburgManagerClientImpl } from '@src/hardy/flensburg/FlensburgManagerClientImpl';
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'stanley';

  readonly navigation = [
    { url: PersonCreate.url, title: PersonCreate.title },
    { url: PersonOverview.url, title: PersonOverview.title }
  ];

  constructor() {
    /*
    let socket = webSocket('ws://localhost:4200/umlp/api/command');
    socket.subscribe(    
      msg => console.log('message received: ' + msg), 
      // Called whenever there is a message from the server    
      err => console.log(err), 
      // Called if WebSocket API signals some kind of error    
      () => console.log('complete') 
      // Called when connection is closed (for whatever reason)  
    );
    /**/
    FlensburgManagerClientImpl.init();
    const cmdManager = new ClientCommandManager();
    cmdManager.ClientCommandManager(new ClientCommandWebsocketCommunication()
      .ClientCommandWebsocketCommunication1('ws://localhost:4200/umlp/api/command'));
    CommandManager.initMe(cmdManager);
    /**/
  }
}
