/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {Location} from '@angular/common';
import {config, UserSignUpComponent} from 'gui/loginmodule/comp/UserSignUpComponent';
import {LoginModuleManager} from 'loginmodule/LoginModuleManager';
import {LoginModuleUser} from 'loginmodule/LoginModuleUser';
import {ClientCommandManager, CommandManager, GemList} from '@umlp/common';
import {LoginSecManager} from "loginmodule/LoginSecManager";
import { GemTextInputValidity } from '@umlp/input';


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

export class UserSignUp extends UserSignUpComponent {
  private error: string = "";

  public static email: RegExp = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;

  public constructor(protected location: Location) {
    super();
  }

  public ngOnInit(): void {
    // LoginModuleManager.loginModuleUserBuilder().then(gemPromiseRes => {
    //   this.newUser = gemPromiseRes;
    // });
  }

  public override navigateBack(): (arg: void) => void {
    return $ => this.location.back();
  }

  public override createUser(): (arg: void) => void {
    return $ => {
      this.error = "";
      if (!this.getUsername() || !this.getPassword() || !this.getEmail()) {
        return;
      }
      let errorMessage = this.validateUser();
      if (!errorMessage) {

        const obj = {};

        obj['email'] = this.email;
        obj['password'] = this.password;
        obj['username'] = this.username;

        const requestOptions: RequestInit = {
          method: "POST",
          body: JSON.stringify(obj),
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
          }
        };
        fetch("/umlp/api/loginmodule/public/signup", requestOptions)
            .then(response => response.json())
            .then(result => {
              // TODO: Improve this
              if (result.success) {
                alert("Successfully created user - TODO: Improve this message :) ")
                this._router.navigate(['gui', 'loginmodule', 'LoginPage']);
              } else if (result.error === 'disabled') {
                this.error = 'SignUps have been disabled. Please contact an administrator.'
                this.setValidityUsername(new GemTextInputValidity('Error', this.error))
              } else {
                console.warn(result);
                this.error = 'Failed to signup: ' + result.error;
                this.setValidityUsername(new GemTextInputValidity('Error', this.error))
              }
            })
            .catch(error => {
              console.warn(error);
              this.error = 'Failed to communicate: ';
            });


      } else {
        this.error = errorMessage;
      }
    }
  }

  public override getErrorValue(): string {
    return this.error;
  }

  private validateUser(): string {
    let res: string = '';
    // Check Email format
    if (!UserSignUp.email.test(this.getEmail())) {
      return 'Wrong format for Email address!';
    }

    return '';
  }

}
