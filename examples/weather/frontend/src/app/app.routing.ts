import { Routes } from "@angular/router";

export const routes: Routes = [
  // default path like in other routing files
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "c/WeatherApp"
  },
  {
    path: "a",
    loadChildren: () => import("../../build/appgui/gui/WeatherViewModule").then(m => m.WeatherviewModule)
  },
  {
    path: "b",
    loadChildren: () => import("../../build/appgui/gui/ForecastUpdateModule").then(m => m.ForecastupdateModule)
  },
  {
    path: "c",
    loadChildren: () => import("../../build/appgui/gui/WeatherAppModule").then(m => m.WeatherappModule)
  },
  {
    path: "d",
    loadChildren: () => import("../../build/appgui/gui/multi_day_forecast/MultiDayForecastAlternativeModule").then(m => m.MultidayforecastalternativeModule)
  }
]
