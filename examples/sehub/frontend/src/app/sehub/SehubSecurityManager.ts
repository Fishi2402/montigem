/* (c) https://github.com/MontiCore/monticore */

import { SehubManager } from '@src/sehub/SehubManager';
import {User} from '@src/sehub/User';

export class SehubSecurityManager {

  private static _currentUser: User = null;

  public static isSignedIn(): boolean {
    return localStorage.getItem('currentUser') !== null;
  }

  public static getCurrentUser(): User {
    SehubSecurityManager._currentUser = new User()
    Object.assign(SehubSecurityManager._currentUser , JSON.parse(localStorage.getItem('currentUser')) as User);
    return SehubSecurityManager._currentUser
  }

  public static setCurrentUser(value: User) {
    SehubSecurityManager._currentUser = value;
    localStorage.setItem('currentUser',JSON.stringify(value))
  }

  public static getCurrentUserIdOrNull(): number | null {
    return SehubSecurityManager.isSignedIn() ? SehubSecurityManager._currentUser.getGemId() : null;
  }

  public static signOut() {
    SehubSecurityManager._currentUser = null;
    localStorage.removeItem('currentUser')
  }

}


