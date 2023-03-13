<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("routes", "existHWCRoutes")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign component = genHelper.getComponentNameFromCamelCaseName(ast.getName())>

// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
import { ${ast.getName()} } from "./${component}/${component}.component";

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