import { Routes } from "@angular/router";

export const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "stanley"
  },
  {
    path: "stanley",
    loadChildren: () => import("./editiere-person-edit/editiere-person-edit.module").then(m => m.EditierePersonEditModule)
  }
]