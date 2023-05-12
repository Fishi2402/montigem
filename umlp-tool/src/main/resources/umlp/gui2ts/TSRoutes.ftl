<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("routes", "existHWCRoutes", "componentPath")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign component = ast.getName()>

// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
import { ${component} } from "${componentPath}";

export const ${routes}<#if existHWCRoutes>TOP</#if>: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "${component}"
  },
  {
    path: "${component}",
    component: ${ast.getName()}
  }
]