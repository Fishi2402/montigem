/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserLoginComponent,config} from '@src/gui/login/UserLoginComponent';
import {User} from '@src/sehub/User';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class UserLogin extends UserLoginComponent {

  public ngOnInit(): void {
    if (SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['gui/Start']);
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
          SehubSecurityManager.setCurrentlyLoggingIng(true);
          // Do NOT use router as otherwise the websocket is not refreshed!
          // The AppComponent will initialize the websocket during reloading
          window.location.href = 'gui/Start';
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
