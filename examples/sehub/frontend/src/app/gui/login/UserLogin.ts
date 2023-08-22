/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserLoginTOP} from '@src/gui/login/UserLoginTOP';
import {User} from "../../../../build/frontendGen/build/appdomain/sehub/User";
import {SehubManager} from "../../../../build/frontendGen/build/appdomain/sehub/SehubManager";
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

@Component({
  selector: 'user-login',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/UserLogin.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class UserLogin extends UserLoginTOP {

  public ngOnInit(): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });

    if (this.loginUser == null) {
      this.loginUser = new User();
    }
  }

  public override login(event: any): void {
    if (!!this.loginUser) {
      for (let ind in this.users.ids) {
        this.users.get(+ind).then(user => {
          if (!!user) {
            if (!!this.loginUser.getUsername() && this.loginUser.getUsername() === user.getUsername() &&
                !!this.loginUser.getPassword() && this.loginUser.getPassword() === user.getPassword()) {
              SehubSecurityManager.setCurrentUser(user);
              this.router.navigate(["../..", "start"]);
            }
          }
        });
      }
    }
  }

}
