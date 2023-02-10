import { Routes } from "@angular/router";
import { PersonEdit } from "./person-edit.component";

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