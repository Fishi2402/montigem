/* (c) https://github.com/MontiCore/monticore */
import { Routes } from "@angular/router";
import { routes as routesTOP } from "@src/AppGemRoutingTOP"

export const routes: Routes = [
  // set default path
  {
    path: "",
    pathMatch: "full",
    redirectTo: "gui/ServiceOverview"
  },
  ...routesTOP
]
