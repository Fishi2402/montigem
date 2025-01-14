/* (c) https://github.com/MontiCore/monticore */

import {User} from 'finanzverwaltung/User';

export class FinanzverwaltungSecurityManager {

  public static getCurrentUser(): User {
    let currentUser: User = new User();
    Object.assign(currentUser, JSON.parse(localStorage.getItem('currentUser')) as User);
    return currentUser;
  }

  public static setCurrentUser(value: User) {
    localStorage.setItem('currentUser', JSON.stringify(value))
  }

  public static getCurrentUserIdOrNull(): number | null {
    return FinanzverwaltungSecurityManager.isSignedIn() ? FinanzverwaltungSecurityManager.getCurrentUser().getGemId() : null;
  }

  public static isCurrentlyLoggingIn(): boolean {
    let item = localStorage.getItem('loggingIn');
    return item !== null && item == "true";
  }

  public static setCurrentlyLoggingIng(val: boolean) {
    localStorage.setItem("loggingIn", val ? "true" : "false");
  }

  public static signIn(value: User) {
    this.setCurrentUser(value);
    this.setCurrentlyLoggingIng(false);
  }

  public static signOut() {
    localStorage.removeItem('currentUser')
  }

  public static isSignedIn(): boolean {
    return localStorage.getItem('currentUser') !== null;
  }

}


