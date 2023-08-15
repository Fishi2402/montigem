import { Routes } from "@angular/router";

export const routes: Routes = [
  // default path like in other routing files
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "login/Login"
  },
  {
    path: "login",
    loadChildren: () => import("../../build/appgui/gui/login/LoginModule").then(m => m.LoginModule)
  },
  {
    path: "login/forgotpassword",
    loadChildren: () => import("../../build/appgui/gui/login/ForgotPasswordModule").then(m => m.ForgotpasswordModule)
  },
  {
    path: "login/registration",
    loadChildren: () => import("../../build/appgui/gui/login/RegistrationModule").then(m => m.RegistrationModule)
  },
  {
    path: "start",
    loadChildren: () => import("../../build/appgui/gui/StartModule").then(m => m.StartModule)
  },
  {
    path: "a",
    loadChildren: () => import("../../build/appgui/gui/SehubMainPageModule").then(m => m.SehubmainpageModule)
  },
  {
    path: "b",
    loadChildren: () => import("../../build/appgui/gui/ProjectPageModule").then(m => m.ProjectpageModule)
  }
]
