import { Routes } from "@angular/router";

export const routes: Routes = [
  // default path like in other routing files
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "person"
  },
  {
    path: "person",
    loadChildren: () => import("../../build/appgui/person/OverviewModule").then(m => m.OverviewModule)
  },
  {
    path: '**',
    redirectTo: 'person'
  }
]
