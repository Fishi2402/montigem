/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserDisplayComponent,config} from '@src/gui/usersettings/UserDisplayComponent';

import {SehubRole} from '@src/sehub/SehubRole';
import {SehubManager} from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {User} from '@src/sehub/User';
import {Option} from '@umlp/input';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class UserDisplay extends UserDisplayComponent {

  public override getRoleOptions(): Array<Option> {
    let res: Array<Option> = [];

    for (let role of Object.values(SehubRole)) {
      if (isNaN(Number(role))) {
        let value = role + '';
        res.push(new Option(value));
      }
    }
    return res;
  }

  public override removeUserButtonLeftClick(event: any) {
    let gemId = this.user.getGemId();
    if (gemId === 1 || (SehubSecurityManager.isSignedIn() && gemId === SehubSecurityManager.getCurrentUser().getGemId())) {
      console.log("Cannot remove main user");
    } else {
      SehubManager.deleteUser(gemId);
    }
  }

  /* public override getErrorroleValue(): string {
     return this.user.getValidityRole().status;
   }*/

  public override getRemoveUserButtonDisabled (): boolean {
    let curUser: User = SehubSecurityManager.getCurrentUser();
    if (!!curUser && SehubRole[curUser.getRole()] === "OWNER") {
      return false;
    }
    return true;
  }

  public override getRoleDisabled (): boolean {
    let curUser: User = SehubSecurityManager.getCurrentUser();
    if (!!curUser && SehubRole[curUser.getRole()] === "OWNER") {
      return false;
    }
    return true;
  }

}
