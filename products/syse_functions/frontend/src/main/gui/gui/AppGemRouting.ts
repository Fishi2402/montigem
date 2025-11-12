
// always imported
import { Route } from "@angular/router";


export const routes: Route[] = [
  {
    path:"",
    loadChildren:() => import("gui/GuiGemRouting").then(m=>m.routes)
  },

]
