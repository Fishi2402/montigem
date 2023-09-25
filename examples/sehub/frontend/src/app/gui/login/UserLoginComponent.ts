/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserLoginComponentTOP} from '@src/gui/login/UserLoginComponentTOP';
import {User} from '@src/sehub/User';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

// import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'user-login',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/login/UserLogin.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["./Login.scss"]
})

export class UserLoginComponent extends UserLoginComponentTOP {

  public ngOnInit(): void {
    if (SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/start']);
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
      redirect: 'manual' // Otherwise the redirect to login?error is followed and 2xx is returned
    };

    fetch("/umlp/api/login", requestOptions)
      .then(response => {
        return response.ok
      })
      .catch(error => {
        console.error(error);
        return false;
      })
      .then(ok => {
        if (ok) {
          SehubSecurityManager.setCurrentlyLoggingIng(true);
          // Do NOT use router as otherwise the websocket is not refreshed!
          // The AppComponent will initialize the websocket during reloading
          window.location.href = '/start';
        } else {
          alert("Invalid Login: Wrong Username or Password!");
        }
      });
  }

  public override getErrorValue(): string {
    return this.error;
  }
}
