<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("astguiCompilationUnits","paths")}
// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
<#list 0..(astguiCompilationUnits?size-1) as index>
  import { ${astguiCompilationUnits[index].getComponentName()} } from "./${paths[index]}";
</#list>


export const route: Routes = [
<#list 0..(astguiCompilationUnits?size-1) as index>
  {
  path:"${paths[index]}",
  component: ${astguiCompilationUnits[index].getComponentName()}
  },
</#list>
]
