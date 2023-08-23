/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {LoginTOP} from '@src/gui/login/LoginTOP';
import { SehubManager } from '@src/sehub/SehubManager';
import {User} from '@src/sehub//User';

@Component({
  selector: 'login',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/Login.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Login.scss"]
})

export class Login extends LoginTOP {

  public ngOnInit(): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });
  }

}
