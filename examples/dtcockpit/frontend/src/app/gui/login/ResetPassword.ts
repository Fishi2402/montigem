/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {ResetPasswordComponent,config} from '@src/gui/login/ResetPasswordComponent';
import {User} from '@src/dtcockpit//User';
import {DtcockpitManager} from '@src/dtcockpit/DtcockpitManager';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: ["./Login.scss"]
})
export class ResetPassword extends ResetPasswordComponent {

  private error: string = "";

  public ngOnInit(): void {
    DtcockpitManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });

    if (this.user == null) {
      this.user = new User();
    }
  }

  public override passwordReset(event: any): (arg: void) => void {
    return $ => {
      if (!this.user || !this.user.getPassword() || !this.user.getEmail()) {
        return;
      }
      this.users.toArray().filter(u => {
        if (!!u && !!this.user.getEmail() && this.user.getEmail() === u.getEmail()) {
          u.setPassword(this.user.getPassword());
          this._router.navigate(['gui', 'login', 'LoginPage']);
        }
      });
      this.error = 'There is no user with the E-mail "' + this.user.getEmail() +'" !';
      this.user.validityEmail = this.error;
      this.user.statusEmail = 'Error';
    }
  }

  public override getErrorValue(): string {
    return this.error;
  }

  public override navigateBack(): (arg: void) => void {
    return $ => this._router.navigate(['gui', 'login', 'LoginPage']);
  }

}
