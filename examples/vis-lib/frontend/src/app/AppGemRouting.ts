
// always imported
import { Routes } from "@angular/router";
// imports module of pages for every package

import { routes as routesTOP } from "@src/AppGemRoutingTOP"
export const routes: Routes = [

  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "person/Overview"
  },
  ...routesTOP,

]
