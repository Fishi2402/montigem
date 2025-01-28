/* (c) https://github.com/MontiCore/monticore */

import {User} from 'dtcockpit/User';
import {LoginSecManager} from "./login/LoginSecManager";
import {LoginSecManager as LoginModuleSecManager} from "loginmodule/LoginSecManager";
import {Router} from "@angular/router";

// Now delegated to the login-modules SecurityManager
export class DtcockpitSecurityManager {

  public static getCurrentUser(): User {
    return LoginSecManager.getCurrentUser();
  }

  public static setCurrentUser(value: User) {
    LoginSecManager.setCurrentUser(value);
  }

  public static getCurrentUserIdOrNull(): number | null {
    return LoginSecManager.getCurrentUserIdOrNull();
  }

  public static isCurrentlyLoggingIn(): boolean {
    return LoginSecManager.isCurrentlyLoggingIn();
  }

  public static setCurrentlyLoggingIng(val: boolean) {
    LoginSecManager.setCurrentlyLoggingIng(val);
  }

  public static signIn(value: User) {
    LoginSecManager.signIn(value);
  }

  public static signOut(router: Router) {
    LoginSecManager.signOut();
    LoginModuleSecManager.getInstance().signOutCurrentUser(router);
  }

  public static isSignedIn(): boolean {
    return LoginSecManager.isSignedIn();
  }

}


