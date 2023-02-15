// HWC-P

import { Routes } from "@angular/router";
// generator may assume, that there is a component translated from Java
import { PersonCarsEdit } from "./person-cars-edit/person-cars-edit.component";
import { routesTOP } from "./personcarsedit-top.routing";

// this file exists to explain TOP mechanism rule for routing
// If the hand-written routing file exists, the generated module imports the
// routes constant from this file instead of generated one.
// The generator may assume that the name of the constant is always "routes"
export const routes: Routes = [
  ...routesTOP,
  {
    path: "extra-path-for-hwc-example",
    component: PersonCarsEdit
  }
]