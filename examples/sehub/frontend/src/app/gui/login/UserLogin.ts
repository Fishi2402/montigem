/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserLoginComponent,config} from 'gui/login/UserLoginComponent';
import {User} from 'sehub/User';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: ["./Login.scss"]
})
export class UserLogin extends UserLoginComponent {

  public ngOnInit(): void {
    super.ngOnInit();
    if (SehubSecurityManager.isSignedIn()) {
      this._router.navigate(['gui/Start']);
    }

    if (this.loginUser == null) {
      this.loginUser = new User();
    }
  }

  protected error: string = "";

  public override loginOnEnter(): (arg: any) => void {
    return (event: KeyboardEvent) => {
      if (event.key === "Enter") {
        this.login();
      }
    }
  }

  public override login(): (arg: void) => void {
    return $ => {
      if (!this.getLoginUser() || !this.getLoginUser().getUsername() || !this.getLoginUser().getPassword()) {
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
  }

  public override getErrorValue(): string {
    return this.error;
  }

}
