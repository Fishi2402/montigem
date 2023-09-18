/* (c) https://github.com/MontiCore/monticore */

import {Directive} from '@angular/core';
import {UserLoginTOP} from '@src/gui/login/UserLoginTOP';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {User} from '@src/sehub/User';
import {SehubManager} from '@src/sehub/SehubManager';
import {
  ClientCommandManager,
  ClientCommandWebsocketCommunication,
  CommandManager
} from '@umlp/common';

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
            body: form,
            redirect: 'manual' // Otherwise the redirect to login?error is followed and 2xx is returned
          };

          fetch("/umlp/api/login", requestOptions)
            .then(response => {
              return response.ok
            })
            .catch(error => {
              console.log(error);
              return false;
            })
            .then(ok => {
              if (ok) {
                this.loginCurUser(this.loginUser);
                const ws_protocol = document.location.protocol == "https:" ? "wss": "ws";
                const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
                    ws_protocol + '://' + document.location.host + '/umlp/api/command'
                ));
                CommandManager.initMe(cmdManager);
                window.location.href = '/start';
              }else{
                alert("Invalid Login: Wrong Username or Password!");
              }
            });

  }

  public override getErrorValue(): string {
    return this.error;
  }

  private loginCurUser(loginInfo : User): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    })

    this.users.toArray().filter(user => {
      if (!!user && loginInfo.getUsername() === user.getUsername()) {
        SehubSecurityManager.signIn(user);
      }
    });
  }

}
