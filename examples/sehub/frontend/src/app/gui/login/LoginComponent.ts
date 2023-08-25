/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {LoginComponentTOP} from '@src/gui/login/LoginTOP';
import { SehubManager } from '@src/sehub/SehubManager';

@Component({
  selector: 'login',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/Login.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Login.scss"]
})

export class LoginComponent extends LoginComponentTOP {

  public ngOnInit(): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });
  }

}
