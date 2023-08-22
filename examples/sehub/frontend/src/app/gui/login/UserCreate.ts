/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserCreateTOP} from '@src/gui/login/UserCreateTOP';
import { SehubRole } from '@src/sehub/SehubRole';
import { ActivationStatus } from '@src/sehub/ActivationStatus';

@Component({
  selector: 'user-create',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/UserCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class UserCreate extends UserCreateTOP {

  public override saveUser(event: any): void {
    //this.userBuilder.activated(ActivationStatus.AKTIVIERT);
    this.userBuilder.registrationDate(new Date());
    this.userBuilder.role(SehubRole.GUEST)
    this.userBuilder.build();
  }

}
