// GEN-P

// this file is generated if there is at least one GUI model describing a page 
// It is always in the top-most folder which is generated from the package names
// Example: package a.b.c; page D() {} -> a.routing.ts is created
// It has slightly different structure from other routing files
import { Routes } from "@angular/router";

export const routes: Routes = [
  // default path like in other routing files
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "personedit"
  },
  {
    // value is a package part which contains at least one page
    path: "personedit",
    // value contains a path to the module and the module name
    loadChildren: () => import("./personedit/personedit.module").then(m => m.PersonEditModule)
  },
  {
    path: "personcarsedit",
    loadChildren: () => import("./personcarsedit/personcarsedit.module").then(m => m.PersonCarsEditModule)
  },
  {
    path: "personoverview",
    loadChildren: () => import("./personoverview/personoverview.module").then(m => m.PersonOverviewModule)
  },
  {
    path: "personcreate",
    loadChildren: () => import("./personcreate/personcreate.module").then(m => m.PersonCreateModule)
  },
  {
    path: "personchildrenedit",
    loadChildren: () => import("./personchildrenedit/personchildrenedit.module").then(m => m.PersonChildrenEditModule)
  },
  {
    path: "test",
    loadChildren: () => import("../../../build/app/stanley/my/InfoBoxModule").then(m => m.InfoboxModule)
  },
  {
    path: "test2",
    loadChildren: () => import("../../../build/app/stanley/personoverview/PersonOverviewModule").then(m => m.PersonoverviewModule)
  },
  {
    path: "test3",
    loadChildren: () => import("../../../build/app/stanley/personcreate/PersonCreateModule").then(m => m.PersoncreateModule)
  },
]