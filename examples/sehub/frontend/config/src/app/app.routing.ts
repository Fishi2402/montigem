import { Routes } from "@angular/router";

export const routes: Routes = [
  // default path like in other routing files
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "Login"
  },
  {
    path: "Gui",
    loadChildren: () => import("../../build/appgui/gui/Gui.Module").then(m => m.Gui_Module)
  },

  {
    path:"Login",
    loadChildren:() => import("../../build/appgui/gui/login/Login.Module").then(m => m.Login_Module)
  },

  {
    path:"Issues",
    loadChildren:() => import("../../build/appgui/gui/issues/Issues.Module").then(m => m.Issues_Module)
  },
  {
    path:"Note",
    loadChildren:() => import("../../build/appgui/gui/note/Note.Module").then(m => m.Note_Module)
  },

  {
    path:"Usersettings",
    loadChildren:() => import("../../build/appgui/gui/usersettings/Usersettings.Module").then(m => m.Usersettings_Module)
  },

  //Woldcard Route for 404 Page:
  {
    path: '**',
    redirectTo: '404'
  }
]
