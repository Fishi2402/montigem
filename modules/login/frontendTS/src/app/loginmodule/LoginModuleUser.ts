/* (c) https://github.com/MontiCore/monticore */

import {LoginModuleUserTOP} from '@src/loginmodule/LoginModuleUserTOP';

export class LoginModuleUser extends LoginModuleUserTOP {
  protected _statusUsername = '';

  protected _validityUsername = '';

  public getHash(value: string): any {
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

  public set validityEmail(value: string) {
    this._validityEmail = value;
  }

  public set statusEmail(value: string) {
    this._statusEmail = value;
  }

}
