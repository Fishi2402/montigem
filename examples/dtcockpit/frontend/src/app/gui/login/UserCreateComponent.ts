/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserCreateComponentTOP} from '@src/gui/login/UserCreateComponentTOP';

import {DtcockpitManager} from '@src/dtcockpit/DtcockpitManager';
import {Location} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'user-create',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/UserCreate.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Login.scss"]
})

export class UserCreateComponent extends UserCreateComponentTOP {

  public constructor(
      protected location: Location,
      protected router: Router) {
    super(router);
  }

  public ngOnInit(): void {
    DtcockpitManager.userBuilder().then(gemPromiseRes => {
      this.newUserBuilder = gemPromiseRes;
    });


    DtcockpitManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
    });


  }

  public override navigateBack(): void {
    this.location.back();
  }
}
