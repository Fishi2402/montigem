/* (c) https://github.com/MontiCore/monticore */

import { Routes } from "@angular/router";
// imports module of pages for every package

import { routes as routesTOP } from "@src/AppGemRoutingTOP"
import { CompositeExample } from "./composite/CompositeExample";
import { ExperimentalExample } from "./experimental/ExperimentalExample";

export const routes: Routes = [
  ...routesTOP,
  {
    path: "composite",
    component: CompositeExample
  },
  {
    path: "experimental",
    component: ExperimentalExample
  },
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "person/Overview"
  }
]
