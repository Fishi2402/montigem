<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("packageComponentRoutings", "defaultRouting", "subRoutes")}
import { Route } from "@angular/router";
<#list packageComponentRoutings as routing>
  import { ${routing.getImportedSymbolName()} } from "${routing.getImportPath()}";
</#list>

<#list subRoutes?keys as key>
  import { routes as ${key} } from "${subRoutes[key]}";
</#list>

export const routes: Route[] = [
<#if defaultRouting??>
  {
    path: "${defaultRouting.getUrlPrefix()}",
    pathMatch: "full",
    redirectTo: "${defaultRouting.getUrl()}"
  },
</#if>
<#list subRoutes?keys as key>
  ...${key},
</#list>
<#list packageComponentRoutings as routing>
  {
    path: "${routing.getUrl()}",
    component: ${routing.getImportedSymbolName()}
  },
</#list>
]
