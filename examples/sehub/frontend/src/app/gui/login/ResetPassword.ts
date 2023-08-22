/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {User} from "../../../../build/frontendGen/build/appdomain/sehub/User";
import {SehubManager} from "../../../../build/frontendGen/build/appdomain/sehub/SehubManager";
import {ResetPasswordTOP} from '@src/gui/login/ResetPasswordTOP';

@Component({
  selector: 'reset-password',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/ResetPassword.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class ResetPassword extends ResetPasswordTOP {

  private error: string = "";

  public ngOnInit(): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });

    if (this.user == null) {
      this.user = new User();
    }
  }

  public override resetPassword(event: any): void {
    if (!this.user || !this.user.getPassword() || !this.user.getEmail()) {
      return;
    }
    for (let ind in this.users.ids) {
      this.users.get(+ind).then(u => {
        if (!!u) {
          if (!!this.user.getEmail() && this.user.getEmail() === u.getEmail()) {
            u.setPassword(this.user.getPassword());
            this.router.navigate(['/login']);
          }
        }
      });
    }
    this.error = 'There is no user with the E-mail ' + this.user.getEmail();
  }

  public override getErrorValue(): string {
    return this.error;
  }

}
