import { Routes } from "@angular/router";

export const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "person-edit"
  },
  {
    path: "person-edit",
    loadChildren: () => import("./person-edit/person-edit.module").then(m => m.PersonEditModule)
  },
  {
    path: "person-cars-edit",
    loadChildren: () => import("./person-cars-edit/person-cars-edit.module").then(m => m.PersonCarsEditModule)
  }
]