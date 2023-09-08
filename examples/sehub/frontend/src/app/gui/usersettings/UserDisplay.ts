/* (c) https://github.com/MontiCore/monticore */

import {Directive} from '@angular/core';
import {UserDisplayTOP} from '@src/gui/usersettings/UserDisplayTOP';

import {SehubRole} from '@src/sehub/SehubRole';
import {SehubManager} from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

@Directive()
export class UserDisplay extends UserDisplayTOP {

  public override getRoleOptions(): Array<string> {
    let res: Array<string> = [];

    for (let role of Object.values(SehubRole)) {
      if (isNaN(Number(role))) {
        res.push(this.enumToCapitalizedString(role + ''))
      }
    }
    return res;
  }

  public override removeUserLeftClick(event: any) {
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

}
