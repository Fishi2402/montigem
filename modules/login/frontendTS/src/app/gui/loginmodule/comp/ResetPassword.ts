/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {config, ResetPasswordComponent} from 'gui/loginmodule/comp/ResetPasswordComponent';
import {LoginModuleManager} from 'loginmodule/LoginModuleManager';
import {GetCurrentUserRequest} from 'loginmodule/commands/GetCurrentUserRequest';
import {ClientCommandManager, CommandManager} from '@umlp/common';
import {LoginSecManager} from "loginmodule/LoginSecManager";


// import { ActivatedRoute, Router } from '@angular/router';
@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  // styleUrls: ["./Login.scss"]
  styleUrls: config.styleUrls,
})
/**
 * Reset the password of a given user.
 */
export class ResetPassword extends ResetPasswordComponent {

  private error: string = "";

  public override passwordReset(): (arg: void) => void {
    return $ => {
      if (!this.user || !this.user.getPassword() || !this.user.getEmail()) {
        return;
      }
      let changed = this.users.filter(u => {
        if (!!u && !!this.user.getEmail() && this.user.getEmail() === u.getEmail()) {
          u.setPassword(this.user.getPassword());
          alert("Successfully changed password of " + u.getUsername())
          return true;
        }
        return false;
      });
      if (!changed) {
        this.error = 'There is no user with the E-mail "' + this.user.getEmail() + '" !';
        this.user.validityEmail = this.error;
        this.user.statusEmail = 'Error';
      }
    }
  }

  public override getErrorValue(): string {
    return this.error;
  }

  public override navigateBack(): (arg: void) => void {
    return $ => this._router.navigate(['gui', 'loginmodule', 'LoginPage']);
  }

}
