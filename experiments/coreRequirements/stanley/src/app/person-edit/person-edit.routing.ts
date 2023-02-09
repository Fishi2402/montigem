import { Routes } from "@angular/router";
import { PersonEdit } from "./person-edit.component";

export const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "editiere-person-edit"
  },
  {
    path: "editiere-person-edit",
    component: PersonEdit
  }
]