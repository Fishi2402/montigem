/* (c) https://github.com/MontiCore/monticore */

import {UserTOP} from '@src/sehub/UserTOP';

export class User extends UserTOP {

  private _statusUsername = '';

  private _validityUsername = '';

  public getValidityUsername(): any {
    return {status: this._statusUsername, message: this._validityUsername};
  }

  public set validityUsername(value: string) {
    this._validityUsername = value;
  }

  public set statusUsername(value: string) {
    this._statusUsername = value;
  }

  private _statusPassword = '';

  private _validityPassword = '';

  public getValidityPassword(): any {
    return {status: this._statusPassword, message: this._validityPassword};
  }

  public set validityPassword(value: string) {
    this._validityPassword = value;
  }

  public set statusPassword(value: string) {
    this._statusPassword = value;
  }

  private _statusEmail = '';

  private _validityEmail = '';

  public getValidityEmail(): any {
    return {status: this._statusEmail, message: this._validityEmail};
  }

  public set validityEmail(value: string) {
    this._validityEmail = value;
  }

  public set statusEmail(value: string) {
    this._statusEmail = value;
  }

  public resetValidity() {
    this.statusUsername = '';
    this.statusEmail = '';
    this.statusPassword = '';
    this.validityUsername = '';
    this.validityEmail = '';
    this.validityPassword = '';
  }

}
