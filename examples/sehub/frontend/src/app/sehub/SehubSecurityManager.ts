/* (c) https://github.com/MontiCore/monticore */

import {User} from '@src/sehub/User';

export class SehubSecurityManager {

  public static isSignedIn(): boolean {
    return localStorage.getItem('currentUser') !== null;
  }

  public static getCurrentUser(): User {
    let currentUser: User = new User();
    Object.assign(currentUser, JSON.parse(localStorage.getItem('currentUser')) as User);
    return currentUser;
  }

  public static setCurrentUser(value: User) {
    localStorage.setItem('currentUser', JSON.stringify(value))
  }

  public static getCurrentUserIdOrNull(): number | null {
    return SehubSecurityManager.isSignedIn() ? SehubSecurityManager.getCurrentUser().getGemId() : null;
  }

  public static signOut() {
    localStorage.removeItem('currentUser')
  }

}


