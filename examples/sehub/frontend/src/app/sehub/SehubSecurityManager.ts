/* (c) https://github.com/MontiCore/monticore */

import {User} from '@src/sehub/User';

export class SehubSecurityManager {

  private static _currentUser: User = null;

  public static getCurrentUser(): User {
    return SehubSecurityManager._currentUser;
  }

  public static setCurrentUser(value: User) {
    SehubSecurityManager._currentUser = value;
  }

}


