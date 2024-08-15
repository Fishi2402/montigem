/* (c) https://github.com/MontiCore/monticore */

import {UserTOP} from '@src/dtcockpit/UserTOP';
import {DtcockpitRole} from '@src/dtcockpit/DtcockpitRole';

export class User extends UserTOP {


  public getAvatar() : any {
    var avatars = 6;
    return ((this.getHash(this.getUsername()) % avatars) + avatars) % avatars;
  }

  private _statusRole = '';

  private _validityRole = '';

  public getValidityRole(): any {
    return {status: this._statusRole, message: this._validityRole};
  }

  public set validityRole(value: string) {
    this._validityRole = value;
  }

  public set statusRole(value: string) {
    this._statusRole = value;
  }

  public override getRole() :DtcockpitRole {
    let res = super.getRole();
    if (!res) {
   //   this.validityRole = 'The role must be set!';
      this.statusRole = 'Error';
    } else {
      this.validityRole = '';
      this.statusRole = '';
    }
    return res;
  }

  public resetValidity() {
    this.statusUsername = '';
    this.statusEmail = '';
    this.statusPassword = '';
    this.validityUsername = '';
    this.validityEmail = '';
    this.validityPassword = '';
    this.validityRole = '';
    this.validityRole = '';
  }

}
