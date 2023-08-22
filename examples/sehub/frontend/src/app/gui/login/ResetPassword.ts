/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {ResetPasswordTOP} from '@src/gui/login/ResetPasswordTOP';

@Component({
  selector: 'reset-password',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/ResetPassword.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class ResetPassword extends ResetPasswordTOP {

  public override resetPassword(event: any): void {
  }

}
