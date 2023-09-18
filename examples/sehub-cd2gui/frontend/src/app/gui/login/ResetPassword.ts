/* (c) https://github.com/MontiCore/monticore */

import {Directive} from '@angular/core';
import {User} from '@src/sehub//User';
import {SehubManager} from '@src/sehub/SehubManager';
import {ResetPasswordTOP} from '@src/gui/login/ResetPasswordTOP';

@Directive()

export class ResetPassword extends ResetPasswordTOP {

  private error: string = "";

  public override resetPassword(event: any): void {
    if (!this.user || !this.user.getPassword() || !this.user.getEmail()) {
      return;
    }
    this.users.toArray().filter(u => {
      if (!!u && !!this.user.getEmail() && this.user.getEmail() === u.getEmail()) {
        u.setPassword(this.user.getPassword());
        this.router.navigate(['/login/LoginPage']);
      }
    });
    this.error = 'There is no user with the E-mail "' + this.user.getEmail() +'" !';
    this.user.validityEmail = this.error;
    this.user.statusEmail = 'Error';
  }

  public override getErrorValue(): string {
    return this.error;
  }

  public override navigateBack(): void {
    this.router.navigate(['/login/LoginPage']);
  }

}
