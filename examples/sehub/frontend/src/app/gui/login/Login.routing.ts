
// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
import { ForgotPasswordPageComponent } from "@src/gui/login/ForgotPasswordPageComponent";
import { LoginPageComponent } from "./LoginPageComponent";
import { ResetPasswordComponent } from "./ResetPasswordComponent";
import { RegistrationPageComponent } from '@src/gui/login/RegistrationPageComponent';
import { UserCreateComponent } from "./UserCreateComponent";
import { UserLoginComponent } from "./UserLoginComponent";

export const routes: Routes = [


  {
    path: "ForgotPasswordPage",
    component: ForgotPasswordPageComponent
  },


  {
    path: "LoginPage",
    component: LoginPageComponent
  },

  {
    path: "",
    pathMatch: "full",
    redirectTo: "LoginPage"
  },

  {
    path: "RegistrationPage",
    component: RegistrationPageComponent
  },
]
