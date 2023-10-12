import { Routes } from "@angular/router";

export const routes: Routes = [
  // default path like in other routing files
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "person/Overview"
  },
  {
    path: "person",
    loadChildren: () => import("../../build/appgui/person/OverviewModule").then(m => m.OverviewModule)
  },
  {
    path: "gui",
    loadChildren: () => import("../../build/appgui/gui/NetworkChartExampleModule").then(m => m.NetworkchartexampleModule)
  }/*,
  {
    path: '**',
    redirectTo: 'person'
  }*/
]
