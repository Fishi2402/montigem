
// always imported
import { Routes } from "@angular/router";
import {Person_Module} from "../../build/frontendGen/build/appgui/person/PersonGemModule";
// imports module of pages for every package


export const routes: Routes = [

  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "person/Overview"
  }

]
