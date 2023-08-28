/* (c) https://github.com/MontiCore/monticore */

import {Directive} from '@angular/core';
import {UserSettingsTOP} from '@src/gui/usersettings/UserSettingsTOP';

@Directive()

export class UserSettings extends UserSettingsTOP {


  public inviteUserLeftClick() {
    this.router.navigate(['/login/registration']);
  }
}
