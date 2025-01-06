/* (c) https://github.com/MontiCore/monticore */

import {UserTOP} from '@src/guidslpaper/UserTOP';
import {Role} from '@src/guidslpaper/Role';

export class User extends UserTOP {

  // TODO: These might be already defined in the LoginModuleUser
  protected _statusUsername = '';

  protected _validityUsername = '';

  public getHash (value: string) : any {
    var hash = 0,
      i, chr;
    if (value.length === 0) return hash;
    for (i = 0; i < value.length; i++) {
      chr = value.charCodeAt(i);
      hash = ((hash << 5) - hash) + chr;
      hash |= 0; // Convert to 32bit integer
    }
    return hash;
  }

  public getValidityUsername(): any {
    return {status: this._statusUsername, message: this._validityUsername};
  }

  public set validityUsername(value: string) {
    this._validityUsername = value;
  }

  public set statusUsername(value: string) {
    this._statusUsername = value;
  }

  protected _statusPassword = '';

  protected _validityPassword = '';

  public getValidityPassword(): any {
    return {status: this._statusPassword, message: this._validityPassword};
  }

  public set validityPassword(value: string) {
    this._validityPassword = value;
  }

  public set statusPassword(value: string) {
    this._statusPassword = value;
  }

  protected _statusEmail = '';

  protected _validityEmail = '';

  public getValidityEmail(): any {
    return {status: this._statusEmail, message: this._validityEmail};
  }

  public getAvatar() : any {
    var avatars = 6;
    return ((this.getHash(this.getUsername()) % avatars) + avatars) % avatars;
  }

  public set validityEmail(value: string) {
    this._validityEmail = value;
  }

  public set statusEmail(value: string) {
    this._statusEmail = value;
  }

  protected _statusRole = '';

  protected _validityRole = '';

  public getValidityRole(): any {
    return {status: this._statusRole, message: this._validityRole};
  }

  public set validityRole(value: string) {
    this._validityRole = value;
  }

  public set statusRole(value: string) {
    this._statusRole = value;
  }

  public override getRole() :Role {
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
