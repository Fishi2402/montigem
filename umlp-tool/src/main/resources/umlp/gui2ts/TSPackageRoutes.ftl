<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("astguiCompilationUnitList","ids","fullPathIndex","paths","modules")}
// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
<#list 0..(astguiCompilationUnitList?size - 1) as index>
  import { ${astguiCompilationUnitList[index].getComponentName()}Component } from "${paths[index]}";
</#list>
<#list modules?keys as key>
  import {${key}_Module} from "./${modules[key]}/${key}.Module";
</#list>



export const routes: Routes = [
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
component: ${astguiCompilationUnitList[index].getComponentName()}Component
},


</#if>


</#list>

<#list modules?keys as key>
  {
  path: "${key}",
  component: ${key}_Module
  },
</#list>
]
