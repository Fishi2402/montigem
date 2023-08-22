/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserSettingsTOP} from '@src/gui/usersettings/UserSettingsTOP';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

@Component({
  selector: 'user-settings',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/usersettings/UserSettings.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["./UserSettings.scss"]
})

export class UserSettings extends UserSettingsTOP {

  public ngOnInit(): void {
    this.currentUser = SehubSecurityManager.getCurrentUser();
    if (this.currentUser !== null) {
      this.currentUserRoutingId = SehubSecurityManager.getCurrentUser().getGemId();
    } else {
      this.router.navigate(['/login']);
    }
  }

  public inviteUserLeftClick() {
    this.router.navigate(['/login/registration']);
  }
}
