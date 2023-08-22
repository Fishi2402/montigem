/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserCreateTOP} from '@src/gui/login/UserCreateTOP';
import {User} from "../../../../build/frontendGen/build/appdomain/sehub/User";
import {SehubRole} from "../../../../build/frontendGen/build/appdomain/sehub/SehubRole";
import { ActivationStatus } from '@src/sehub/ActivationStatus';

import {SehubManager} from "../../../../build/frontendGen/build/appdomain/sehub/SehubManager";
import { GemList } from '../../../../build/frontendGen/node_modules/@umlp/common/lib/common/GemList';

@Component({
  selector: 'user-create',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/UserCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class UserCreate extends UserCreateTOP {

  private error: string = "";

  private allUser: User[] = [];

  private users: GemList<User>;

  public ngOnInit(): void {
    SehubManager.userBuilder().then(gemPromiseRes => {
      this.newUserBuilder = gemPromiseRes;
    });

    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
     /* for (let ind in this.users.ids) {  // TODO: remove this after synchronizing does properly
        this.users.get(+ind).then(user => {
          if (!!user) {
            this.allUser.push(user);
            console.log('\n User:' + user.getUsername());
          }
        });
      }*/
    });

  }

  public override createUser(event: any): void {
    this.error = "";
    let errorMessage = this.validateUser();
    if (!errorMessage) {
      //this.userBuilder.activated(ActivationStatus.AKTIVIERT);
      this.newUserBuilder.registrationDate(new Date());
      this.newUserBuilder.role(SehubRole.DEVELOPER);
      this.build();
      this.router.navigate(["..", "login"]);
    } else {
      this.error = errorMessage + ' already being used';
    }

  }

  public override getErrorValue(): string {
    return this.error;
  }

  private validateUser(): String {
    let res: string = '';
    this.allUser.filter(user => {
      if (!!this.getNewUser().getUsername() && this.getNewUser().getUsername() === user.getUsername()) {
        res += ' Account ' + this.getNewUser().getUsername();
      }
      if (!!this.getNewUser().getEmail() && this.getNewUser().getEmail() === user.getEmail()) {
        res += (res.length === 0? '': ' und ') + ' E-mail ' + this.getNewUser().getEmail();
      }
    });
    return res;
  }
}
