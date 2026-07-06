/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemTextInputValidity } from '@umlp/input';
import { config, ForgotPasswordComponent } from '../../../../build/gui2java2ts/montigem/ForgotPasswordComponent';


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
              const validity = new GemTextInputValidity()
              validity._status = 'Error';
              validity._message = 'Password resets have been disabled. Please contact an administrator.';
              this.setValidity(validity)
              this.setErrorMessage('Password resets have been disabled. Please contact an administrator.')
            } else {
              console.warn(result);
              const validity = new GemTextInputValidity()
              validity._status = 'Error';
              validity._message = 'Password resets have been disabled. Please contact an administrator.';
              this.setValidity(validity)
              this.setValidity(validity)
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
