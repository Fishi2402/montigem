/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { UserSettingsTOP } from '@src/gui/usersettings/UserSettingsTOP';

@Component({
  selector: 'user-settings',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/usersettings/UserSettings.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})


/* Hookpoint: ClassContent:Annotations */

export  class UserSettings extends UserSettingsTOP {
  public createNewUserLeftClick(): void {
    console.log("HI");
  }
}
