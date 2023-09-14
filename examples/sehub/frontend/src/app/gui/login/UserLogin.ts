/* (c) https://github.com/MontiCore/monticore */

import {Directive} from '@angular/core';
import {UserLoginTOP} from '@src/gui/login/UserLoginTOP';
import {User} from '@src/sehub/User';
import {SehubManager} from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/common';
import {SehubManagerClientImpl} from '@src/sehub/SehubManagerClientImpl';

@Directive()

export class UserLogin extends UserLoginTOP {

  protected error: string = "";

  public override loginOnEnter(event: KeyboardEvent): void {
    if (event.key === "Enter") {
      this.login();
    }
  }

  public override login(): void {
    if (!this.loginUser || !this.loginUser.getUsername() || !this.loginUser.getPassword()) {
      return;
    }

          const form = new FormData();

          form.append('username', this.loginUser.getUsername());
          form.append('password', this.loginUser.getPassword());

          const requestOptions: RequestInit = {
              method: "POST",
              body: form
//               redirect: "manual"
            };

          fetch("/umlp/api/login", requestOptions)
              //.then(response => response.json())
              .then(result => console.log(result))
              .then(result => this.initWS())
              //.then(result => SehubSecurityManager.setCurrentUser(user))
              .then(result => this.router.navigate(['/start']))
              .catch(error => console.log(error.message))
              .catch(error => alert("Invalid login"));
  }

  public override getErrorValue(): string {
    return this.error;
  }
   public initWS(){
       SehubManagerClientImpl.init();
       const ws_protocol = document.location.protocol == "https:" ? "wss": "ws";
       const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
         ws_protocol + '://' + document.location.host + '/umlp/api/command'
       ));
       CommandManager.initMe(cmdManager);
 }

}
