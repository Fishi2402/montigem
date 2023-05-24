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
    redirectTo: "test"
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
  {
    path: "test4",
    loadChildren: () => import("../../../build/app/stanley/personedit/PersonEditModule").then(m => m.PersoneditModule)
  },
  {
    path: "test5",
    loadChildren: () => import("../../../build/app/stanley/gui/AccountCreateModule").then(m => m.AccountcreateModule)
  },
]