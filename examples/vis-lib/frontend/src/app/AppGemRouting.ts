/* (c) https://github.com/MontiCore/monticore */

import { Routes } from "@angular/router";
import { routes as routesTOP } from "@src/AppGemRoutingTOP";

export const routes: Routes = [
  ...routesTOP,
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "person/Overview"
  }
]
