/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserSettingsComponent,config} from '@src/gui/usersettings/UserSettingsComponent';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {SehubManager} from '@src/sehub/SehubManager';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
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
