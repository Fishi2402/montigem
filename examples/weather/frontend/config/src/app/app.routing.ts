import { Routes } from "@angular/router";
import {WeatherApp} from "../../build/appgui/gui/WeatherApp";
import { ForecastUpdate } from "@src/gui/ForecastUpdate";
import {WeatherView } from ""@src/gui/WeatherView"
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
    component:WeatherView
  },
  {
    path: "b",
    component: ForecastUpdate
  },
  {
    path: "c",
    component: WeatherApp
  }
]
