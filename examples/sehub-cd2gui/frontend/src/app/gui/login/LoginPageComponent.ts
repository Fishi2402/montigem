/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {LoginPageComponentTOP} from '@src/gui/login/LoginPageComponentTOP';
import { SehubManager } from '@src/sehub/SehubManager';

@Component({
  selector: 'login',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/LoginPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["./Login.scss"]
})

export class LoginPageComponent extends LoginPageComponentTOP {

  public ngOnInit(): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });
  }

}
