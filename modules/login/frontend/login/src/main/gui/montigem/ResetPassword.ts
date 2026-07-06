/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { config, ResetPasswordComponent } from '../../../../build/gui2java2ts/montigem/ResetPasswordComponent';
import { LoginModuleUser } from '../loginmodule/LoginModuleUser';
import { GemTextInputValidity } from '@umlp/input';


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

  public override getEmailValidity(user: LoginModuleUser): GemTextInputValidity {
    return user.getValidityEmail();
  }

  public override passwordReset(): (arg: void) => void {
    return $ => {
      if (!this.user || !this.user.getPassword() || !this.user.getEmail()) {
        return;
      }
      this.users.toArrayDeepSync().then(users => {
        const changed = users.some(u => {
          if (u && this.user!.getEmail() === u.getEmail()) {
            u.setPassword(this.user!.getPassword());
            alert(`Successfully changed password of ${u.getUsername()}`);
            return true;
          }
          return false;
        });

        if (!changed) {
          this.error = `There is no user with the E-mail "${this.user!.getEmail()}"!`;
          this.user!.validityEmail = this.error;
          this.user!.statusEmail = 'Error';
        }
      }).catch(err => {
        console.error('Failed to load users', err);
        this.error = 'Failed to load users.';
      });
    };
  }

  public override getErrorValue(): string {
    return this.error;
  }

  public override navigateBack(): (arg: void) => void {
    return $ => this._router.navigate(['gui', 'loginmodule', 'LoginPage']);
  }

}
