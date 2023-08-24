<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("astguiCompilationUnits","paths","topNameMap")}
// always imported
import { Routes } from "@angular/router";
// imports module of pages for every package
<#list 0..(astguiCompilationUnits?size-1) as index>
  import { ${astguiCompilationUnits[index].getComponentName()}} from "${paths[index]}";
</#list>


export const routes: Routes = [
<#list 0..(astguiCompilationUnits?size-1) as index>
  {
  path:"${paths[index]+astguiCompilationUnits[index].getComponentName()?lower_case}",
  component: ${astguiCompilationUnits[index].getComponentName()}

  },
</#list>
]
