<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("astguiCompilationUnitList", "existHWC", "ids","fullPathIndex","paths","routingLinkPrefix")}
// always imported
import { Routes } from "@angular/router";
<#list 0..(astguiCompilationUnitList?size - 1) as index>
  import { ${astguiCompilationUnitList[index].getComponentName()}<#if !existHWC[index]>Component</#if> } from "${paths[index]}";
</#list>



export const routes: Routes = [
<#list 0..(astguiCompilationUnitList?size - 1) as index>
    <#if astguiCompilationUnitList[index].getGUIComponentDeclaration().isPage() >

    <#if index==fullPathIndex>
      {
      path: "${routingLinkPrefix}",
      pathMatch: "full",
      redirectTo: "${routingLinkPrefix}/${astguiCompilationUnitList[index].getComponentName()}"
      },
    </#if>

      <#if routingLinkPrefix!="">
     {
     path: "${routingLinkPrefix}/${astguiCompilationUnitList[index].getComponentName()}<#list ids[index] as id>/:${id}</#list>",
     component: ${astguiCompilationUnitList[index].getComponentName()}<#if !existHWC[index]>Component</#if>
     },
      </#if>
        <#if routingLinkPrefix=="">
          {
          path: "${astguiCompilationUnitList[index].getComponentName()}<#list ids[index] as id>/:${id}</#list>",
          component: ${astguiCompilationUnitList[index].getComponentName()}<#if !existHWC[index]>Component</#if>
          },
        </#if>


</#if>


</#list>

]
