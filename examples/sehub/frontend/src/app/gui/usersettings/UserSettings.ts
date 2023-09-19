/* (c) https://github.com/MontiCore/monticore */

import {Directive} from '@angular/core';
import {UserSettingsTOP} from '@src/gui/usersettings/UserSettingsTOP';
import {SehubRole} from '@src/sehub/SehubRole';
import {User} from '@src/sehub/User';

@Directive()

export class UserSettings extends UserSettingsTOP {


  public inviteUserLeftClick() {
    this.router.navigate(['/login/registration']);
  }

  public override getInviteUserDisabled (): boolean {
    let curUser: User = this.getUsers().toArray().filter(user =>
      !!user && this.getCurrentUser().getUsername() === user.getUsername())[0];

    if (!!curUser && (SehubRole[curUser.getRole()] === "OWNER" ||
                      SehubRole[curUser.getRole()] === "MAINTAINER")) {
      return false;
    }

    return true;
  }

}
