
import { Route } from "@angular/router";
import { routes as routesTOP } from "AppGemRoutingTOP"

export const routes: Route[] = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "common/LandingPage"
  },
  ...routesTOP
];