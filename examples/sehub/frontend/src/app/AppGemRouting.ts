import { Routes } from "@angular/router";
import { routes as routesTOP } from "@src/gui/GuiGemRoutingTOP"
export const routes: Routes = [
  // default path like in other routing files
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "gui/login/LoginPage"
  },
    ...routesTOP
  ]
