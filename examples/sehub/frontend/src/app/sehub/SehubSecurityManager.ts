/* (c) https://github.com/MontiCore/monticore */

import {User} from '@src/sehub/User';

export class SehubSecurityManager {

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

  public static signIn() {
    localStorage.setItem('isSignedIn', JSON.stringify(true))
  }

  public static signOut() {
    localStorage.setItem('isSignedIn', JSON.stringify(false))
  }

  public static isSignedIn(): boolean {
    if (localStorage.getItem('isSignedIn') === null) {
      localStorage.setItem('isSignedIn', JSON.stringify(false))
    }
    return (JSON.parse(localStorage.getItem('isSignedIn')) as boolean);
  }

}


