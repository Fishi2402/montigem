
// always imported
import { Routes } from "@angular/router";
// imports module of pages for every package


export const routes: Routes = [
{
path:"gui",
loadChildren:() => import("../../build/frontendGen/build/appgui/gui/GuiGemModule").then(m => m.Gui_Module)
},
]
