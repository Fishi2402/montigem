/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {config, ForgotPasswordComponent} from '@src/gui/loginmodule/comp/ForgotPasswordComponent';
import {LoginModuleManager} from '@src/loginmodule/LoginModuleManager';
import {GetCurrentUserRequest} from '@src/loginmodule/commands/GetCurrentUserRequest';
import {ClientCommandManager, CommandManager} from '@umlp/common';
import {LoginSecManager} from "@src/loginmodule/LoginSecManager";


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
 * Reset the password of any user
 */
export class ForgotPassword extends ForgotPasswordComponent {

  public override requestPasswordReset(): (arg: void) => void {
    return () => {
      this.setValidity(null);
      this.setErrorMessage(null);
      if (!this.email) {
        return;
      }
      const obj = {};

      obj['email'] = this.email;

      const requestOptions: RequestInit = {
        method: "POST",
        body: JSON.stringify(obj),
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        }
      };
      fetch("/umlp/api/loginmodule/public/reset_password", requestOptions)
          .then(response => response.json())
          .then(result => {
            // TODO: Improve this
            if (result.success) {
              alert("Got success - TODO: Improve this message :) ")
            } else if (result.error === 'disabled') {
              this.setValidity({
                'status': 'Error',
                'message': 'Password resets have been disabled. Please contact an administrator.'
              })
              this.setErrorMessage('Password resets have been disabled. Please contact an administrator.')
            } else {
              console.warn(result);
              this.setValidity({'status': 'Error', 'message': 'Failed to request password reset'})
              this.setErrorMessage('Failed to request password reset: ' + result.error);
            }
          })
          .catch(error => {
            console.warn(error);
            alert("Failed to communicate");
          });
    };
  }

}
