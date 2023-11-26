/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserCreateComponent} from '@src/gui/login/UserCreateComponent';
import {DtcockpitRole} from '@src/dtcockpit/DtcockpitRole';
import {User} from "@src/dtcockpit/User";
import {GemList} from '@umlp/common';
import {DtcockpitManager} from '@src/dtcockpit/DtcockpitManager';
import {Location} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'user-create',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/UserCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Login.scss"]
})
export class UserCreate extends UserCreateComponent {

  private error: string = "";

  protected users: GemList<User>;

  public static email: RegExp = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;

  public constructor(
      protected location: Location,
      protected router: Router) {
    super(router);
  }

  public ngOnInit(): void {
    DtcockpitManager.userBuilder().then(gemPromiseRes => {
      this.newUserBuilder = gemPromiseRes;
    });
  }

  public override navigateBack(): void {
    this.location.back();
  }

  public override createUser(event: any): void {
    this.error = "";
    if (!this.getNewUser().getUsername() || !this.getNewUser().getPassword() || !this.getNewUser().getEmail()) {
      return;
    }
    let errorMessage = this.validateUser();
    if (!errorMessage) {
      //this.userBuilder.activated(ActivationStatus.AKTIVIERT);
      this.newUserBuilder.registrationDate(new Date());
      this.newUserBuilder.role(DtcockpitRole.USER);
      this.build();
      this.router.navigate(['gui', 'login']);
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
        res += (res.length === 0 ? '' : ' und ') + ' E-mail "' + this.getNewUser().getEmail() + '"';
      }
    });
    return res + (res.length === 0 ? '' : ' already being used!');
  }

}
