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
      this._router.navigate(['gui/login/LoginPage']);
    }
    this.currentUserRoutingId = SehubSecurityManager.getCurrentUserIdOrNull();

    super.ngOnInit();
  }

  public inviteUserLeftClick(): (arg: void) => void {
    return $ => this._router.navigate(['gui/login/RegistrationPage']);
  }
}
