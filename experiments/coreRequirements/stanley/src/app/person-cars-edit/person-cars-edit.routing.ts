import { Routes } from "@angular/router";
import { PersonCarsEdit } from "./person-cars-edit.component";
import { routesTOP } from "./person-cars-edit-top.routing";

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