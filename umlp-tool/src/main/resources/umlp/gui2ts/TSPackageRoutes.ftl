<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("astguiCompilationUnitList","ids","fullPathIndex","topNameMap","paths")}
// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
<#list 0..(astguiCompilationUnitList?size - 1) as index>
  import { ${astguiCompilationUnitList[index].getComponentName()} } from "${topNameMap[astguiCompilationUnitList[index].getComponentName()]}${paths[index]}${astguiCompilationUnitList[index].getComponentName()}";
</#list>

export const route: Routes = [
<#list 0..(astguiCompilationUnitList?size - 1) as index>
    <#if astguiCompilationUnitList[index].getGUIComponentDeclaration().isPage() >

    <#if index==fullPathIndex>
      {
      path: "",
      pathMatch: "full",
      redirectTo: "${astguiCompilationUnitList[index].getComponentName()}"
      },
    </#if>

{
path: "${astguiCompilationUnitList[index].getComponentName()}<#list ids[index] as id>/:${id}</#list>",
component: ${astguiCompilationUnitList[index].getComponentName()}
},
</#if>
</#list>
]
