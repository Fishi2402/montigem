<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a module ts file from the given model -->
${tc.signature("modules")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign viewComponents = glex.getGlobalVar("viewComponents")>
<#assign component = genHelper.getComponentNameFromCamelCaseName(ast.getName())>

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { routes } from "./${ast.getName()?lower_case}.routing";
import { ${ast.getName()} } from "./${component}/${component}.component";
<#list modules as module>
import { ${module?cap_first}Module } from "src/app/${viewComponents.getPackageNameForModule(module)}/${module}.module";  <#--../tmp/io/io.module-->
</#list>

@NgModule({
  declarations: [
    ${ast.getName()}
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
<#list modules as module>
    ${module?cap_first}Module, <#--IoModule-->
</#list>
  ],
  exports: [
    ${ast.getName()}
  ]
})

export class ${ast.getName()}Module { }
