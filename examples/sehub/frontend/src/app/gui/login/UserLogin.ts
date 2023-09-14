/* (c) https://github.com/MontiCore/monticore */

import {Directive} from '@angular/core';
import {UserLoginTOP} from '@src/gui/login/UserLoginTOP';
import {User} from '@src/sehub/User';
import {SehubManager} from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

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
            };

          fetch("/umlp/api/login", requestOptions)
            .then(response => response.json())
            .then(result => {
              console.log(result.toString());
              return true;
            })
            .catch(error => {
              console.log(error);
              alert("Invalid Login: Wrong Username or Password!");
              return false;
            })
            .then(redirect => {
              if (redirect) {
                SehubSecurityManager.signIn();
                window.location.href = '/start';
              }
            });

  }

  public override getErrorValue(): string {
    return this.error;
  }

}
