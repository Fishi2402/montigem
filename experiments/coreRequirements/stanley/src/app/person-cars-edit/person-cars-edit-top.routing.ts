import { Routes } from "@angular/router";
import { PersonCarsEdit } from "./person-cars-edit.component";

// this file exists to explain TOP mechanism rule for routing
export const routesTOP: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "person-cars-edit"
  },
  {
    path: "person-cars-edit",
    component: PersonCarsEdit
  }
]