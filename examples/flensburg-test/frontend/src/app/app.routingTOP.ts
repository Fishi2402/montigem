
// always imported
import { Routes } from "@angular/router";
// imports module of pages for every package


export const routes: Routes = [
{
path:"gui",
loadChildren:() => import("../../build/appgui/gui/Gui.Module").then(m => m.Gui_Module)
},
]
