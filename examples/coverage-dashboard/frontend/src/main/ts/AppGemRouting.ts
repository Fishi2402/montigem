/* (c) https://github.com/MontiCore/monticore */
import { Route } from "@angular/router";
import { routes as routesTOP } from "AppGemRoutingTOP"


export const routes: Route[] = [
  {
    path:"",
    pathMatch: "full",
    redirectTo:"Dashboard"
  },
  ...routesTOP
]
