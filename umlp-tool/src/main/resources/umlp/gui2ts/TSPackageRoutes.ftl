<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("astguiCompilationUnitList","ids","fullPathIndex","paths","pathName")}
// always imported
import { Routes } from "@angular/router";
<#list 0..(astguiCompilationUnitList?size - 1) as index>
  import { ${astguiCompilationUnitList[index].getComponentName()}Component } from "${paths[index]}";
</#list>



export const routes: Routes = [
<#list 0..(astguiCompilationUnitList?size - 1) as index>
    <#if astguiCompilationUnitList[index].getGUIComponentDeclaration().isPage() >

    <#if index==fullPathIndex>
      {
      path: "${pathName}",
      pathMatch: "full",
      redirectTo: "${astguiCompilationUnitList[index].getComponentName()}"
      },
    </#if>

     {
     path: "${pathName}/${astguiCompilationUnitList[index].getComponentName()}<#list ids[index] as id>/:${id}</#list>",
     component: ${astguiCompilationUnitList[index].getComponentName()}Component
     },


</#if>


</#list>

]
