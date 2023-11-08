/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserLoginComponentTOP} from '@src/gui/login/UserLoginComponentTOP';
import {User} from '@src/dtcockpit/User';
import {DtcockpitSecurityManager} from "../../dtcockpit/DtcockpitSecurityManager";

// import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'user-login',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/UserLogin.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["./Login.scss"]
})

export class UserLoginComponent extends UserLoginComponentTOP {

  public ngOnInit(): void {
    if (DtcockpitSecurityManager.isSignedIn()) {
      this.router.navigate(['gui', 'Start']);
    }

    if (this.loginUser == null) {
      this.loginUser = new User();
    }
  }

  protected error: string = "";

  public override loginOnEnter(event: KeyboardEvent): void {
    if (event.key === "Enter") {
      this.login();
    }
  }

  public override login(): void {
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
          DtcockpitSecurityManager.setCurrentlyLoggingIng(true);
          // Do NOT use router as otherwise the websocket is not refreshed!
          // The AppComponent will initialize the websocket during reloading
          window.location.href = '/gui/Start';
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

  public override getErrorValue(): string {
    return this.error;
  }
}
