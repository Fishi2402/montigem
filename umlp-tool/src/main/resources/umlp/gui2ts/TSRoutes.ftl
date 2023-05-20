<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("routes", "existHWCRoutes", "componentPath", "ids")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign component = ast.getName()>
<#assign idBased = ids?has_content>

// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
import { ${component} } from "${componentPath}";

export const ${routes}<#if existHWCRoutes>TOP</#if>: Routes = [
<#if !idBased>
  {
    path: "",
    pathMatch: "full",
    redirectTo: "${component}"
  },
</#if>
  {
    path: "${component}<#list ids as id>/:${id}</#list>",
    component: ${ast.getName()}
  }
]