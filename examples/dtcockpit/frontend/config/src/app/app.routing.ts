import {Routes} from "@angular/router";

export const routes: Routes = [
  // default path like in other routing files
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "login/LoginPage"
  },
  {
    path: "login",
    loadChildren: () => import("../../build/appgui/gui/login/LoginPageModule").then(m => m.LoginpageModule)
  },
  {
    path: "login/forgotpassword",
    loadChildren: () => import("../../build/appgui/gui/login/ForgotPasswordPageModule").then(m => m.ForgotpasswordpageModule)
  },
  {
    path: "login/registration",
    loadChildren: () => import("../../build/appgui/gui/login/RegistrationPageModule").then(m => m.RegistrationpageModule)
  },
  {
    path: "start",
    loadChildren: () => import("../../build/appgui/gui/StartModule").then(m => m.StartModule)
  },
  {
    path: '404',
    loadChildren: () => import("../../build/appgui/gui/PageNotFoundModule").then(m => m.PagenotfoundModule)
  },

  //Woldcard Route for 404 Page:
  {
    path: '**',
    redirectTo: '404'
  }
]
