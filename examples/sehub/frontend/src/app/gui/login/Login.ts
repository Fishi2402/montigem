/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {LoginTOP} from '@src/gui/login/LoginTOP';
import {SehubManager} from "../../../../build/frontendGen/build/appdomain/sehub/SehubManager";
import {User} from "../../../../build/frontendGen/build/appdomain/sehub/User";

@Component({
  selector: 'login',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/Login.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class Login extends LoginTOP {

  public ngOnInit(): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });
  }

  public  getGemText6Value (us: User): string  {
    return us.getUsername() + '';
  }

}
