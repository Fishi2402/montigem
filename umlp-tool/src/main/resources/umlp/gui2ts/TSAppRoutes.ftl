<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("routingModules","defaultRouting")}
// always imported
import { Route } from "@angular/router";


export const routes: Route[] = [
 <#if defaultRouting!="">
 {
   path:"",
   pathMatch: "full",
   redirectTo:"${routingModules[defaultRouting]}"
 },
<#list routingModules?keys as key>
  {
  path:"${routingModules[key]}",
  loadChildren:() => import("./${routingModules[key]}/${key}GemRouting").then(m=>m.routes)
  },

</#list>
</#if>
]
