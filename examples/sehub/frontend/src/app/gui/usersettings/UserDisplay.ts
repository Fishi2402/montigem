/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { UserDisplayTOP } from '@src/gui/usersettings/UserDisplayTOP';

import { SehubRole } from '@src/sehub/SehubRole';

@Component({
  selector: 'user-display',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/usersettings/UserDisplay.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class UserDisplay extends UserDisplayTOP {

  public  getRoleValue (): string
  {
    if (!(this.getUser()!=null&&this.getUser()!=null)) {
      return null;
    }
    let res = SehubRole[this.getUser().getRole()] + '';
    res = res.toLowerCase();
    res = res.charAt(0).toUpperCase() + res.slice(1);
    return res;
  }

}
