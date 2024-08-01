<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from a given package -->
${tc.signature("packageRoute", "componentRoutes", "defaultRoute")}
<#assign subRoutes = packageRoute.getChildren()>
import { Route } from "@angular/router";
<#list componentRoutes as route>
  import { ${route.getImportedSymbolName()} } from "${route.getImportPathNoExt()}";
</#list>

<#list subRoutes as route>
  import { routes as ${route.getAlias()} } from "${route.getImportPath()}";
</#list>

export const routes: Route[] = [
<#if defaultRoute.isPresent()>
  {
    path: "<#if packageRoute.getParent().isPresent()>${defaultRoute.get().getUrlPrefix()}</#if>",
    pathMatch: "full",
    redirectTo: "${defaultRoute.get().getUrl()}"
  },
</#if>
<#list subRoutes as route>
  ...${route.getAlias()},
</#list>
<#list componentRoutes as route>
  {
    path: "${route.getUrl()}",
    component: ${route.getImportedSymbolName()}
  },
</#list>
]
