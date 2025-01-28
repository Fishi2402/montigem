/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserCreateComponent,config} from 'gui/login/UserCreateComponent';
import {Role} from 'guidslpaper/Role';
import {User} from "guidslpaper/User";
import {GemList} from '@umlp/common';
import {GuidslpaperManager} from 'guidslpaper/GuidslpaperManager';
import {Location} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: ["./Login.scss"]
})

export class UserCreate extends UserCreateComponent {

  private error: string = "";

  protected users: GemList<User>;

  public static email: RegExp = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;

  public constructor(protected location: Location) {
    super();
  }

  public ngOnInit(): void {
    GuidslpaperManager.userBuilder().then(gemPromiseRes => {
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
        this.newUser.setRole(Role.USER);
        this.newUser.build();
        this._router.navigate(['gui', 'login']);
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
    this.users.toArray().filter(user => {
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
