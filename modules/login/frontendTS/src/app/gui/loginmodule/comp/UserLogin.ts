/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {config, UserLoginComponent} from 'gui/loginmodule/comp/UserLoginComponent';
import {LoginModuleManager} from 'loginmodule/LoginModuleManager';
import {GetCurrentUserRequest} from 'loginmodule/commands/GetCurrentUserRequest';
// import {LoginSecurityManager} from "../../loginmodule/DtcockpitSecurityManager";
import {ClientCommandManager, CommandManager} from '@umlp/common';
import {LoginSecManager} from "loginmodule/LoginSecManager";


// import { ActivatedRoute, Router } from '@angular/router';
@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  // styleUrls: ["./Login.scss"]
  styleUrls: config.styleUrls,
})

export class UserLogin extends UserLoginComponent {

  public ngOnInit(): void {
    if (LoginSecManager.isSignedIn()) {
      // this._router.navigate(['gui', 'Start']);
      alert("Apparently already logged in");
      return;
    }
    this.loadModuleConfigFromPublic();
    if (this.loginUser == null) {
      this.loginUser = LoginModuleManager.loginModuleUserBuilderFromId(-32);
      // this.loginUser = new LoginModuleUser(); // TODO: builder? constructor?
    }

    if (LoginSecManager.getInstance().shouldBeLoggedIn()) {
      let me = this;
      LoginSecManager.getInstance().waitForLazyLogin().then(value => {
        alert("Apparently already logged in, but slow");
      }).catch(this.ngOnInitContinue.bind(me));
      return;
    }
    this.ngOnInitContinue()

  }

  protected ngOnInitContinue() {
    (CommandManager.getInstance() as ClientCommandManager).pauseRetryCommunication();
  }

  protected loadModuleConfigFromPublic() {
    const requestOptions: RequestInit = {
      method: "GET",
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      }
    };
    const that = this;
    fetch("/umlp/api/loginmodule/public/config", requestOptions)
        .then(response => response.json())
        .then(result => {
          console.warn(result);
          that.setSignupEnabled(result.signup);
          that.setForgotPasswordEnabled(result.reset);
        });
  }

  protected error: string = "";

  public override loginOnEnter(): (event: KeyboardEvent) => void {
    return (event: KeyboardEvent) => {
      if (event.key === "Enter") {
        this.login();
      }
    }
  }

  public override login(): (arg: void) => void {
    return $ => {
      if (!this.loginUser || !this.loginUser.getUsername() || !this.loginUser.getPassword()) {
        return;
      }

      const form = new FormData();

      form.append('username', this.loginUser.getUsername());
      form.append('password', this.loginUser.getPassword());

      const requestOptions: RequestInit = {
        method: "POST",
        body: form,
        redirect: 'manual', // Otherwise the redirect to login?error is followed and 2xx is returned
        headers: {
          'Accept': 'application/json'
        }
      };

      fetch("/umlp/api/login", requestOptions)
          .then(response => response.json())
          .then(result => {
            if (result.success) {
              // LoginSecManager.setCurrentlyLoggingIng(true);
              // As the WS is retrying, we only hav
              (CommandManager.getInstance() as ClientCommandManager).retryInitCommunicationImmediate();

              console.log("Sending command")
              // Fetch the current Users gemId
              CommandManager.executeCommand(new GetCurrentUserRequest()).then(res => {
                let sr = res.asSimpleResult();
                if (sr.isSuccessful()) {
                  const currentUserId: number = sr.get();
                  // and then fetch the User object
                  LoginModuleManager.getLoginModuleUserSure(currentUserId).then(currentUser => {
                    // to store it in the LoginSecManager
                    LoginSecManager.getInstance().finishLoginAs(currentUser);
                    this._router.navigate(['gui', 'Start']);
                  })
                } else {
                  alert("Failed GetCurrentUser from backend side");
                }
              }).catch(reason => {
                alert("failed GetCurrentUserRequest");
                console.error(reason)
              })
            } else if (result.reason) {
              alert("Login Failed: " + result.reason)
            } else {
              alert("Login Failed: Unknown")
            }
          })
          .catch(error => {
            console.warn(error);
            alert("Login Failed: Unable to communicate");
          });
    }
  }

  public override getErrorValue(): string {
    return this.error;
  }

  public override navigateForgotPassword(): (arg: void) => void {
    return () => { this._router.navigate(['gui', 'loginmodule', 'ForgotPasswordPage']);};
  }

  public override navigateSignUp(): (arg: void) => void {
    return () => { this._router.navigate(['gui', 'loginmodule', 'SignupPage']);};
  }
}
