/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserLoginComponent} from '@src/gui/login/UserLoginComponent';
import {User} from '@src/sehub/User';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

@Component({
  selector: 'user-login',
  templateUrl: '../../../..//build/frontendGen/build/appgui/gui/login/UserLogin.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
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
            headers: {
              'Accept': 'application/json'
            }
          };

          fetch("/umlp/api/login", requestOptions)
            .then(response => response.json())
            .then(result => {
              if (result.success) {
                SehubSecurityManager.signIn(this.loginUser);
                window.location.href = 'gui/Start';
              }else if (result.reason) {
                alert("Login Failed: " + result.reason)
              }else{
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
