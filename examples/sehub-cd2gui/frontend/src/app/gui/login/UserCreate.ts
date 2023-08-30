/* (c) https://github.com/MontiCore/monticore */

import {Directive} from '@angular/core';
import {UserCreateTOP} from '@src/gui/login/UserCreateTOP';
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';
import { ActivationStatus } from '@src/sehub/ActivationStatus';

import { SehubManager } from '@src/sehub/SehubManager';
import { GemList } from '../../../../build/frontendGen/node_modules/@umlp/common/lib/common/GemList';

@Directive()

export class UserCreate extends UserCreateTOP {

  protected error: string = "";

  protected users: GemList<User>;

  public static email: RegExp = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;

  public override createUser(event: any): void {
    this.error = "";
    if (!this.getNewUser().getUsername() || !this.getNewUser().getPassword() || !this.getNewUser().getEmail()) {
      return;
    }
    let errorMessage = this.validateUser();
    if (!errorMessage) {
      //this.userBuilder.activated(ActivationStatus.AKTIVIERT);
      this.newUserBuilder.registrationDate(new Date());
      this.newUserBuilder.role(SehubRole.DEVELOPER);
      this.build();
      this.router.navigate(['/login']);
    } else {
      this.error = errorMessage;
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
        res += (res.length === 0? '': ' und ') + ' E-mail "' + this.getNewUser().getEmail() + '"';
      }
    });
    return res + (res.length === 0? '': ' already being used!');
  }

  public override navigateBack(): void {
    this.router.navigate(['/login']);
  }

}
