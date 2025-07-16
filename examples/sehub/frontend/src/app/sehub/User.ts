/* (c) https://github.com/MontiCore/monticore */

import {UserTOP} from 'sehub/UserTOP';
import { SehubRole } from 'sehub/SehubRole';
import {GemTextInputValidity} from '@umlp/input';

export class User extends UserTOP {

  private _statusUsername = '';

  private _validityUsername = '';

  public getValidityUsername(): GemTextInputValidity {
    return new GemTextInputValidity(this._statusUsername, this._validityUsername);
  }

  public set validityUsername(value: string) {
    this._validityUsername = value;
  }

  public set statusUsername(value: string) {
    this._statusUsername = value;
  }

  private _statusPassword = '';

  private _validityPassword = '';

  public getValidityPassword(): GemTextInputValidity {
    return new GemTextInputValidity(this._statusPassword, this._validityPassword);
  }

  public set validityPassword(value: string) {
    this._validityPassword = value;
  }

  public set statusPassword(value: string) {
    this._statusPassword = value;
  }

  private _statusEmail = '';

  private _validityEmail = '';

  public getValidityEmail(): GemTextInputValidity {
    return new GemTextInputValidity(this._statusEmail, this._validityEmail);
  }

  public set validityEmail(value: string) {
    this._validityEmail = value;
  }

  public set statusEmail(value: string) {
    this._statusEmail = value;
  }

  private _statusRole = '';

  private _validityRole = '';

  public getValidityRole(): GemTextInputValidity {
    return new GemTextInputValidity(this._statusRole, this._validityRole);
  }

  public set validityRole(value: string) {
    this._validityRole = value;
  }

  public set statusRole(value: string) {
    this._statusRole = value;
  }

  public override getRole() :SehubRole {
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
