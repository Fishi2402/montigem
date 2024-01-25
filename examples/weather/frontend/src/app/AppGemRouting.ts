/* (c) https://github.com/MontiCore/monticore */
import { Route } from "@angular/router";
  // default path like in other routing files
export const routes: Route[] = [
  {path: 'gui', loadChildren: () => import('./gui/GuiGemRouting').then(r=>r.routes)},
  // ...
]
