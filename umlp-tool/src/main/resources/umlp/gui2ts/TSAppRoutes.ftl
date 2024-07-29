<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("routeMap", "defaultRouting")}
import { Route } from "@angular/router";

export const routes: Route[] = [
<#if defaultRouting != "">
  {
    path: "",
    pathMatch: "full",
    redirectTo: "${defaultRouting}"
  },
</#if>
<#list routeMap?keys as key>
  {
    path: "${routeMap[key].getURLSegment()}",
    loadChildren: () => import("${key}").then(m => m.routes)
  },
</#list>
];