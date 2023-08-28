/* (c) https://github.com/MontiCore/monticore */

import {Directive} from '@angular/core';
import {UserLoginTOP} from '@src/gui/login/UserLoginTOP';
import {User} from '@src/sehub/User';
import {SehubManager} from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

@Directive()

export class UserLogin extends UserLoginTOP {

  private error: string = "";

  public override login(event: any): void {
    if (!this.loginUser || !this.loginUser.getUsername() || !this.loginUser.getPassword()) {
      return;
    }
    this.error = ``;
    this.loginUser.resetValidity();
    this.users.toArray().filter(user => {
      if (!!user && this.loginUser.getUsername() === user.getUsername()) {
        if (this.loginUser.getPassword() === user.getPassword()) {
          SehubSecurityManager.setCurrentUser(user);
          this.router.navigate(['/start']);
        } else {
          this.error = 'Wrong password!';
          this.loginUser.validityPassword = this.error;
          this.loginUser.statusPassword = 'Error';
        }
      }
    });
    if (!this.error) {
      this.error = 'There is no user "' + this.loginUser.getUsername() + '" !';
      this.loginUser.validityUsername = this.error;
      this.loginUser.statusUsername = 'Error';
    }
  }

  public override getErrorValue(): string {
    return this.error;
  }

}
