/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {LoginPageComponent} from '@src/gui/login/LoginPageComponent';
import { SehubManager } from '@src/sehub/SehubManager';
import {config} from "@src/gui/login/LoginPageStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'login',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/LoginPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["./Login.scss"]
})

export class LoginPage extends LoginPageComponent {

  public ngOnInit(): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });
  }

}
