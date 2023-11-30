/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {User} from '@src/dtcockpit//User';
import {DtcockpitManager} from '@src/dtcockpit/DtcockpitManager';
import {ResetPasswordComponentTOP} from '@src/gui/login/ResetPasswordComponentTOP';

@Component({
  selector: 'reset-password',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/ResetPassword.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Login.scss"]
})

export class ResetPasswordComponent extends ResetPasswordComponentTOP {

  public ngOnInit(): void {
    DtcockpitManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
      this.users.loadAll();
    });

    if (this.user == null) {
      this.user = new User();
    }
  }

}
