import { Routes } from "@angular/router";
import { PersonCarsEdit } from "./person-cars-edit.component";
import { routesTOP } from "./person-cars-edit-top.routing";

export const routes: Routes = [
  ...routesTOP,
  {
    path: "extra-path-for-hwc-example",
    component: PersonCarsEdit
  }
]