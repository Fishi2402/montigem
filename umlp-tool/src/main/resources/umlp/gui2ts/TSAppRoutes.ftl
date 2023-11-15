<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("routingModules","defaultRouting")}
// always imported
import { Routes } from "@angular/router";


export const routes: Routes = [
 <#if defaultRouting!="">
 {
   path:"",
   pathMatch: "full",
   redirectTo:"${routingModules[defaultRouting]}"
 },
<#list routingModules?keys as key>
  {
  path:"${routingModules[key]}",
  loadChildren:() => import("./${routingModules[key]}/${key}GemModule").then(m=>m.${key}_Module)
  },

</#list>
</#if>
]
