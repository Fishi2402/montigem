/* (c) https://github.com/MontiCore/monticore */
import { Routes } from "@angular/router";
import { routes as routesTOP } from "@src/AppGemRoutingTOP"

export const routes: Routes = [
  // default path like in other routing files
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "gui/kitchen/KitchenPage"
  },
  ...routesTOP
]
