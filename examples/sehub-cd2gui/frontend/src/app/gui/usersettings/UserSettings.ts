/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserSettingsComponent} from '@src/gui/usersettings/UserSettingsComponent';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {SehubManager} from '@src/sehub/SehubManager';
import {config} from "@src/gui/usersettings/UserSettingsStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'user-settings',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/usersettings/UserSettings.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./UserSettings.scss',]
})
export class UserSettings extends UserSettingsComponent {

  private changePasswordValue = "";

  public ngOnInit(): void {
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['gui/login/LoginPage']);
    }
    this.currentUser = SehubSecurityManager.getCurrentUser();
    this.currentUserRoutingId = SehubSecurityManager.getCurrentUserIdOrNull();

    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
    });

    super.ngOnInit();
  }

  public inviteUserLeftClick() {
    this.router.navigate(['gui/login/RegistrationPage']);
  }
}
