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
    path: "basic",
    loadChildren: () => import("../../build/appgui/basic/GemButtonExampleModule").then(m => m.GembuttonexampleModule)

  },
  {
      path: "basic",
      loadChildren: () => import("../../build/appgui/basic/GemAudioExampleModule").then(m => m.GemaudioexampleModule)

  },
  {
     path: "basic",
     loadChildren: () => import("../../build/appgui/basic/GemCheckboxExampleModule").then(m => m.GemcheckboxexampleModule)

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
