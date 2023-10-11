<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature()}
// always imported
import { Routes } from "@angular/router";


export const routes: Routes = [
{
path:"gui",
loadChildren:() => import("./gui/GuiGemModule").then(m => m.Gui_Module)
},
]
