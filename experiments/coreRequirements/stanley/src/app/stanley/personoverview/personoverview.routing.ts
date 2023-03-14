// GEN-P

import { Routes } from "@angular/router";
import { PersonOverview } from "./person-overview/person-overview.component";

export const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "person-overview"
  },
  {
    path: "person-overview",
    component: PersonOverview
  }
]