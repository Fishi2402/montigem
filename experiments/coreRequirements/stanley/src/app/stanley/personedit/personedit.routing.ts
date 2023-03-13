

// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
import { PersonEdit } from "./person-edit/person-edit.component";

export const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "person-edit"
  },
  {
    path: "person-edit",
    component: PersonEdit
  }
]