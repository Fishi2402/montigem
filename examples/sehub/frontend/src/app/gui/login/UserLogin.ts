/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserLoginTOP} from '@src/gui/login/UserLoginTOP';
import {User} from '@src/sehub/User';
import {SehubManager} from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

@Component({
  selector: 'user-login',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/UserLogin.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Login.scss"]
})

export class UserLogin extends UserLoginTOP {

  private error: string = "";

  public ngOnInit(): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });

    if (this.loginUser == null) {
      this.loginUser = new User();
    }
  }

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
