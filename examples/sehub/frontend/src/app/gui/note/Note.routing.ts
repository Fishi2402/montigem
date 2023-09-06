
// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
  import { NoteComponent } from "./NoteComponent";
  import { ResponseComponent } from "@src/gui/note/ResponseComponent";

export const routes: Routes = [
  {
    path: "Note/:postRoutingId",
    component: NoteComponent
  },
  {
    path: "Response/:postRoutingId",
    component: ResponseComponent
  }
]
