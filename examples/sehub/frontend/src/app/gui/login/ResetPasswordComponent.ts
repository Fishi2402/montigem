/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {User} from '@src/sehub//User';
import {SehubManager} from '@src/sehub/SehubManager';
import {ResetPasswordComponentTOP} from '@src/gui/login/ResetPasswordComponentTOP';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'reset-password',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/ResetPassword.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Login.scss"]
})

export class ResetPasswordComponent extends ResetPasswordComponentTOP {

  public ngOnInit(): void {
    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });

    if (this.user == null) {
      this.user = new User();
    }
  }

}
