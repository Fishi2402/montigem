/* (c) https://github.com/MontiCore/monticore */

import {User} from '@src/sehub/User';

export class SehubSecurityManager {

  private static _currentUser: User = null;

  public static isSignedIn(): boolean {
    return SehubSecurityManager._currentUser !== null;
  }

  public static getCurrentUser(): User {
    return SehubSecurityManager._currentUser;
  }

  public static setCurrentUser(value: User) {
    SehubSecurityManager._currentUser = value;
  }

  public static getCurrentUserIdOrNull(): number | null {
    return SehubSecurityManager.isSignedIn() ? SehubSecurityManager._currentUser.getGemId() : null;
  }

  public static signOut() {
    SehubSecurityManager._currentUser = null;
  }

}


