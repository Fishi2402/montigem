// GEN-P

// File is created for each package part which contains a page (not just a component).
// Example:
// package a.b; page C(){} -> routing file "b.routing(-top).ts" is created, but not "a.routing.ts"
// this file explains TOP mechanism rule for routing, see HWC file

// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
import { PersonCarsEdit } from "./person-cars-edit/person-cars-edit.component";

export const routesTOP: Routes = [
  // The first entry always contains following:
  // { path: "", pathMatch: "full", redirectTo: <value> }
  // where <value> takes one of the "path" values from the following entries (random one)
  // Used in an app to make sure that every sensible path leads somewhere,
  // isn't used in the code as is.
  {
    path: "",
    pathMatch: "full",
    redirectTo: "person-cars-edit"
  },
  // Entry is created for each page in the package (not just a component)
  {
    path: "person-cars-edit",
    component: PersonCarsEdit
  }
]