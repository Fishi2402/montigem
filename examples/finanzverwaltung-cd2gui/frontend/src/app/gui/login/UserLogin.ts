/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserLoginComponent,config} from '@src/gui/login/UserLoginComponent';
import {User} from '@src/finanzverwaltung/User';
import {FinanzverwaltungSecurityManager} from "../../finanzverwaltung/FinanzverwaltungSecurityManager";

// import { ActivatedRoute, Router } from '@angular/router';

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: ["./Login.scss"]
})

export class UserLogin extends UserLoginComponent {

  public ngOnInit(): void {
    if (FinanzverwaltungSecurityManager.isSignedIn()) {
      this._router.navigate(['gui', 'Start']);
    }

    if (this.loginUser == null) {
      this.loginUser = new User();
    }
  }

  protected error: string = "";

  public override loginOnEnter(): (arg: KeyboardEvent) => void {
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
            FinanzverwaltungSecurityManager.setCurrentlyLoggingIng(true);
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
  }

  public override getErrorValue(): string {
    return this.error;
  }
}
