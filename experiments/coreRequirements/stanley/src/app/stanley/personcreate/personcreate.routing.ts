

// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
import { PersonCreate } from "./person-create/person-create.component";

export const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "person-create"
  },
  {
    path: "person-create",
    component: PersonCreate
  }
]