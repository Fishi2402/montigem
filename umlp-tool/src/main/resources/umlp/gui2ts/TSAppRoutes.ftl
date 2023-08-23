<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("astguiCompilationUnits","paths")}
// always imported
import { Routes } from "@angular/router";
// imports module of pages for every package
<#list astguiCompilationUnits as ast>
  import { ${ast.getComponentName()}${topNameMap[ast.getComponentName()]} } from "./${ast.getComponentName()}${topNameMap[ast.getComponentName()]}";
</#list>


export const route: Routes = [
<#list 0..(astguiCompilationUnits?size-1) as index>
  {
  path:"${paths[index]+astguiCompilationUnits[index].getComponentName()?lower_case}",
  component: ${astguiCompilationUnits[index].getComponentName()}${topNameMap[astguiCompilationUnits[index].getComponentName()]}

  },
</#list>
]
