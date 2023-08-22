/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserDisplayTOP} from '@src/gui/usersettings/UserDisplayTOP';

import {SehubRole} from '@src/sehub/SehubRole';
import {SehubManager} from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

@Component({
  selector: 'user-display',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/usersettings/UserDisplay.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/UserDisplay.scss"]
})
export class UserDisplay extends UserDisplayTOP {

  public override getRoleEntry(): string {
    if (!(this.getUser() != null && this.getUser() != null)) {
      return null;
    }
    return this.enumToString(SehubRole[this.getUser().getRole()] + '');
  }

  private enumToString(res: string) {
    res = res.toLowerCase();
    res = res.charAt(0).toUpperCase() + res.slice(1);
    return res;
  }

  public override getRoleOptions(): Array<string> {
    let res: Array<string> = [];

    for (let role of Object.values(SehubRole)) {
      if (isNaN(Number(role))) {
        res.push(this.enumToString(role + ''))
      }
    }

    return res;
  }

  public override setRoleEntry(value: string): void {
    if (!(this.getUser() != null && this.getUser() != null)) {
      return null;
    }
    if (value == null || value == undefined || value == "") {
      return null;
    }
    let sehubRoleElement = SehubRole[value.toUpperCase()];
    this.getUser().setRole(sehubRoleElement);
  }

  public override removeUserLeftClick(event: any) {
    let gemId = this.user.getGemId();
    if (gemId === 1 || gemId === SehubSecurityManager.getCurrentUser().getGemId()) {
      console.log("Cannot remove main user");
    } else {
      SehubManager.deleteUser(gemId);
    }
  }

}
