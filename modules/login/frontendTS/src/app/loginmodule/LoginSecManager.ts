/* (c) https://github.com/MontiCore/monticore */

import {LoginModuleUser} from 'loginmodule/LoginModuleUser';
import {LoginModuleManager} from 'loginmodule/LoginModuleManager';
import {LoginSecManagerTOP} from 'loginmodule/LoginSecManagerTOP';

import {ActivatedRoute, Router} from "@angular/router";
import {CommandManager, NotificationScope} from "@umlp/common";
import {GetCurrentUserRequest} from 'loginmodule/commands/GetCurrentUserRequest';

export enum LAZY_LOGIN_STATE {
  LOGIN_DONE,

  FAIL_CONNECTION,
  FAIL_PERMISSION
}

export class LoginSecManager extends LoginSecManagerTOP {

  private static _instance: LoginSecManager = undefined;

  static readonly LS_KEY: string = "MightBeLoggedIn";

  public finishLoginAs(currentUser: LoginModuleUser) {
    // User logged in
    localStorage.setItem(LoginSecManager.LS_KEY, "true");
    this.setSignedInUser1(currentUser);
  }

  public static setInstance(value: LoginSecManager) {
    this._instance = value;
  }

  public static getInstance(): LoginSecManager {
    if (LoginSecManager._instance === undefined)
      LoginSecManager._instance = new LoginSecManager();
    return LoginSecManager._instance;
  }

  public static shouldAbortOnInit(router: Router, route?: ActivatedRoute): boolean {
    return this.getInstance()._shouldAbortOnInit(router, route);
  }


  public _shouldAbortOnInit(router: Router, route?: ActivatedRoute): boolean {
    if (LoginSecManager.isSignedIn()) {
      // Connection already established
      return false;
    }
    if (this.shouldBeLoggedIn()) {
      // We should be logged in, but appear to not be yet (either due to just reloading the page or restarting the backend)
      this.ensureLoginHappens(router, route)

      return false;
    }
    this.redirectToLoginPage(router, route);
    return true; // Abort ngOnInit further
  }

  public shouldBeLoggedIn(): boolean {
    let item = localStorage.getItem(LoginSecManager.LS_KEY);
    return item !== null && item == "true";
  }

  public static isSignedIn(): boolean {
    return this.getInstance()._isSignedIn();
  }

  public _isSignedIn(): boolean {
    return LoginSecManager.getInstance().getSignedInUser() && true;
  }


  public redirectToLoginPage(router: Router, route?: ActivatedRoute): void {
    console.log("[LoginSecManager] RedirectingToLoginPage, was on ", route)
    console.log("user:", LoginSecManager.getInstance().getSignedInUser());
    if (route)
      console.log("-> ", route.snapshot.url);
    localStorage.removeItem(LoginSecManager.LS_KEY);
    router.navigate(['gui', 'loginmodule', 'LoginPage']); // TODO: Store path in global var
  }

  public signOutCurrentUser(router: Router): void {
    fetch("/umlp/api/logout").finally(() => {
      LoginSecManager.getInstance().setSignedInUserAbsent(NotificationScope.LOCAL);
      router.navigate(['gui', 'loginmodule', 'LoginPage']); // TODO: Store path in global var
    });
  }

  /**
   * Wait until:
   *  - GetCurrentUserRequest succeeds => resolve(LOGIN_DONE)
   *  - The Backend is not reachable after 3 seconds => reject(CONNECTION)
   *  - The Backend is reachable after 3 seconds (only the WebSocket was not allowed) => reject(PERMISSION)
   */
  public waitForLazyLogin(): Promise<LAZY_LOGIN_STATE> {
    return new Promise<LAZY_LOGIN_STATE>((resolve, reject) => {


      // Ask the backend for our identity
      const ensureLogin = CommandManager.executeCommand(new GetCurrentUserRequest()).then(async res => {
        let sr = res.asSimpleResult();
        if (sr.isSuccessful()) {
          const currentUserId: number = sr.get();
          // and then fetch the User object
          const currentUser = await LoginModuleManager.getLoginModuleUserSure(currentUserId);
          // to store it in the LoginSecManager
          LoginSecManager.getInstance().finishLoginAs(currentUser);
          return LAZY_LOGIN_STATE.LOGIN_DONE;
        } else {
          throw new Error("Failed GetCurrentUser from backend side");
        }
      })


      // Prepare a timeout (as on 501s, the connection is just never opened and all messages are queued into oblivion)
      const timeout: Promise<never> = new Promise((_, reject) => {
        setTimeout(() => reject("TIMEOUT"), 1000 * 3)
      })
      // Problem: Both a network connection issue and auth issue look the same to the frontend (due to the WebSocket spec)
      // Thus, also prepare a fetch to the api to see if the backend is actually online
      Promise.race([ensureLogin, Promise.allSettled([timeout, this.retry(this.waitForAPIIsAvailable)])])
          .then(v => {
            if (v === LAZY_LOGIN_STATE.LOGIN_DONE) {
              // Success -> User was set
              resolve(LAZY_LOGIN_STATE.LOGIN_DONE);
            } else {
              const psr = v as [PromiseSettledResult<never>, PromiseSettledResult<number>]
              if (psr[1].status === "rejected") {
                reject(LAZY_LOGIN_STATE.FAIL_CONNECTION);
              } else {
                reject(LAZY_LOGIN_STATE.FAIL_PERMISSION);
              }
            }
          });
    });
  }

  protected ensureLoginHappens(router: Router, route?: ActivatedRoute) {
    this.waitForLazyLogin().then(value => {
      if (value === LAZY_LOGIN_STATE.LOGIN_DONE) {
        // Success -> User was set
      } else {
        alert("Unknown login ensured: " + value);
      }
    }).catch(reason => {
      switch (reason) {
        case LAZY_LOGIN_STATE.FAIL_PERMISSION:
          // Backend available, but command timed out
          this.redirectToLoginPage(router, route);
          break;
        case LAZY_LOGIN_STATE.FAIL_CONNECTION:
          // Backend is not available => show generic error message
          alert("Unable to connect to the server.\nReloading the web application.\nPlease ensure, that you are connected to the internet.");
          location.reload()
          break;
        default:
          alert("Unknown login failure ensured: " + reason);
      }
    })
  }

  // Test if the command endpoint is available
  protected waitForAPIIsAvailable(timeout = 3000): Promise<number> {
    // TODO: Expose via the ClientCommandManager?

    return Promise.race(
        [new Promise<number>((resolve, rej) => {
          const commandEndpoint = document.location.protocol + '//' + document.location.host + '/umlp/api/command';
          fetch(commandEndpoint, {signal: AbortSignal.timeout(timeout)}).then(value => {
            if (value.ok)
              resolve(value.status);
            else
              rej(value.status)
          })
        }),
          // abort timeout (the AbortController does not reject the promise)
          new Promise<number>((_, rej) => setTimeout(rej, timeout))
        ]);
  }

  // Retry a promise maxAttempts time
  protected retry(fn: (() => any), maxAttempts = 3, delay = 10, attemptNr = 0) {
    return new Promise((resolve, _) => setTimeout(resolve, delay))
        .then(fn)
        .catch(err => {
          if (attemptNr < maxAttempts) {
            return this.retry(fn, maxAttempts, delay, attemptNr + 1)
          }
          throw err
        })

  }

}


