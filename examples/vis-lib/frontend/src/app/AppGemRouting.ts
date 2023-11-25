
// always imported
import { Routes } from "@angular/router";
// imports module of pages for every package


export const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
    redirectTo: "person/Overview"
  },
  {
   path: "example_arrange",
   pathMatch: "full",
    // redirect to a module, not a component (use package part instead of the component name)
   redirectTo: "example_arrange/GemCardExample"
  },
    {
     path: "example_arrange",
     pathMatch: "full",
      // redirect to a module, not a component (use package part instead of the component name)
     redirectTo: "example_arrange/GemColumnExample"
    },
    {
     path: "example_arrange",
     pathMatch: "full",
     // redirect to a module, not a component (use package part instead of the component name)
     redirectTo: "example_arrange/GemRowExample"
    },
      {
         path: "example_table",
         pathMatch: "full",
         // redirect to a module, not a component (use package part instead of the component name)
         redirectTo: "example_table/GemTableExample"
      },
      {
       path: "example_input",
       pathMatch: "full",
       // redirect to a module, not a component (use package part instead of the component name)
       redirectTo: "example_input/GemInputTextExample"
      }

]
