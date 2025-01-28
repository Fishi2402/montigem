/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserDisplayComponent,config} from 'gui/usersettings/UserDisplayComponent';

import {SehubRole} from 'sehub/SehubRole';
import {SehubManager} from 'sehub/SehubManager';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {User} from 'sehub/User';
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

}
