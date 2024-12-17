/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {Location} from '@angular/common';
import {config, UserCreateComponent} from '@src/gui/loginmodule/comp/UserCreateComponent';
import {LoginModuleManager} from '@src/loginmodule/LoginModuleManager';
import {LoginModuleUser} from '@src/loginmodule/LoginModuleUser';
import {ClientCommandManager, CommandManager, GemList} from '@umlp/common';
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

export class UserCreate extends UserCreateComponent {
  private error: string = "";

  public static email: RegExp = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;

  public constructor(protected location: Location) {
    super();
  }

  public ngOnInit(): void {
    LoginModuleManager.loginModuleUserBuilder().then(gemPromiseRes => {
      this.newUser = gemPromiseRes;
    });
  }

  public override navigateBack(): (arg: void) => void {
    return $ => this.location.back();
  }

  public override createUser(): (arg: void) => void {
    return $ => {
      this.error = "";
      if (!this.getNewUser().getUsername() || !this.getNewUser().getPassword() || !this.getNewUser().getEmail()) {
        return;
      }
      let errorMessage = this.validateUser();
      if (!errorMessage) {
        //this.user.activated(ActivationStatus.AKTIVIERT);
        this.newUser.setRegistrationDate(new Date());
        // this.newUser.setRole(DtcockpitRole.USER);
        this.newUser.build();
        this._router.navigate(['gui', 'loginmodule', 'LoginPage']);
      } else {
        this.error = errorMessage;
      }
    }
  }

  public override getErrorValue(): string {
    return this.error;
  }

  private validateUser(): string {
    let res: string = '';
    // Check Email format
    if (!UserCreate.email.test(this.getNewUser().getEmail())) {
      return 'Wrong format for Email address!';
    }

    // Check if user is unique
    this.users.filter(user => {
      if (this.getNewUser().getUsername() === user.getUsername()) {
        res += ' Account "' + this.getNewUser().getUsername() + '"';
      }
      if (this.getNewUser().getEmail() === user.getEmail()) {
        res += (res.length === 0 ? '' : ' und ') + ' E-mail "' + this.getNewUser().getEmail() + '"';
      }
    });
    return res + (res.length === 0 ? '' : ' already being used!');
  }

}
