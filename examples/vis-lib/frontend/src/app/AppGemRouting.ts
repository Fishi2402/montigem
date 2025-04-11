/* (c) https://github.com/MontiCore/monticore */

import { Routes } from "@angular/router";
// imports module of pages for every package

import { routes as routesTOP } from "AppGemRoutingTOP"

export const routes: Routes = [
  ...routesTOP,
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "person/Overview"
  }
]
