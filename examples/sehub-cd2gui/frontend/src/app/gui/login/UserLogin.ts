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
            body: form,
            headers: {
              'Accept': 'application/json'
            }
          };

          fetch("/umlp/api/login", requestOptions)
            .then(response => response.json())
            .then(result => {
              if (result.success) {
                SehubSecurityManager.signIn(this.loginUser);
                window.location.href = '/start';
              }else if (result.reason) {
                alert("Login Failed: " + result.reason)
              }else{
                alert("Login Failed: Unknown")
              }
            })
            .catch(error => {
              console.warn(error);
              alert("Login Failed: Unable to communicate");
            });

  }

  public override getErrorValue(): string {
    return this.error;
  }

}
