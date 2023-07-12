import { Routes } from "@angular/router";

export const routes: Routes = [
  // default path like in other routing files
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "a"
  },
  {
    path: "a",
    loadChildren: () => import("../../build/appgui/gui/WeatherViewModule").then(m => m.WeatherviewModule)
  },
  {
    path: "b",
    loadChildren: () => import("../../build/appgui/gui/ForecastUpdateModule").then(m => m.ForecastupdateModule)
  },
]