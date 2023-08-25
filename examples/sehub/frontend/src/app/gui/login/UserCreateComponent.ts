/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserCreateComponentTOP} from '@src/gui/login/UserCreateComponentTOP';
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';
import { ActivationStatus } from '@src/sehub/ActivationStatus';

import { SehubManager } from '@src/sehub/SehubManager';
import { GemList } from '../../../../build/frontendGen/node_modules/@umlp/common/lib/common/GemList';

@Component({
  selector: 'user-create',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/UserCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Login.scss"]
})

export class UserCreateComponent extends UserCreateComponentTOP {

  public ngOnInit(): void {
    SehubManager.userBuilder().then(gemPromiseRes => {
      this.newUserBuilder = gemPromiseRes;
    });

    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
    });

  }
}
