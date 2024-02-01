/* (c) https://github.com/MontiCore/monticore */

import { Routes } from "@angular/router";
// imports module of pages for every package

import { routes as routesTOP } from "@src/AppGemRoutingTOP"
export const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "gui/Start"
  },
  ...routesTOP,
  // {
  //   path:"cd2gui",
  //   loadChildren:() => import('../../build/umlp/fe2/cd2gui/models/ModelsGemRouting').then(m=>m.routes)
  // },
]
