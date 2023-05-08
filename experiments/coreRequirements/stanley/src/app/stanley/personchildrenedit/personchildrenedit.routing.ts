// GEN-P

import { Routes } from "@angular/router";
import { PersonChildrenEdit } from "./person-children-edit/person-children-edit.component";

export const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "person-children-edit"
  },
  {
    path: "person-children-edit",
    component: PersonChildrenEdit
  }
]