/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserLoginComponentTOP} from '@src/gui/login/UserLoginComponentTOP';
import {User} from '@src/sehub/User';
import {SehubManager} from '@src/sehub/SehubManager';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'user-login',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/UserLogin.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Login.scss"]
})

export class UserLoginComponent extends UserLoginComponentTOP {

  public constructor(route: ActivatedRoute, router: Router) {
    super(route, router);
  }

  public ngOnInit(): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });

    if (this.loginUser == null) {
      this.loginUser = new User();
    }
  }
}
