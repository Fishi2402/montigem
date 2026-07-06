/* (c) https://github.com/MontiCore/monticore */

import { LoginModuleUser as LoginModuleUserTOP } from '../../../../build/umlpJava2ts/loginmodule/LoginModuleUser';
import { GemTextInputValidity } from '@umlp/input';

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

  public getValidityUsername(): GemTextInputValidity {
    const validity = new GemTextInputValidity()
    validity._status = this._statusUsername;
    validity._message = this._validityUsername;
    return validity;
  }

  public set validityUsername(value: string) {
    this._validityUsername = value;
  }

  public set statusUsername(value: string) {
    this._statusUsername = value;
  }

  protected _statusPassword = '';

  protected _validityPassword = '';

  public getValidityPassword(): GemTextInputValidity {
    const validity = new GemTextInputValidity()
    validity._status = this._statusPassword;
    validity._message = this._validityPassword;
    return validity;
  }

  public set validityPassword(value: string) {
    this._validityPassword = value;
  }

  public set statusPassword(value: string) {
    this._statusPassword = value;
  }

  protected _statusEmail = '';

  protected _validityEmail = '';

  public getValidityEmail(): GemTextInputValidity {
    const validity = new GemTextInputValidity()
    validity._status = this._statusEmail;
    validity._message = this._validityEmail;
    return validity;
  }

  public set validityEmail(value: string) {
    this._validityEmail = value;
  }

  public set statusEmail(value: string) {
    this._statusEmail = value;
  }

}
