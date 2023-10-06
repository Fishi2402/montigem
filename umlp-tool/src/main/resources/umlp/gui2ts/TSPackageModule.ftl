<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("astguiCompilationUnitList","ids","compDecl","paths","existHWCModules","pathHWModule","pathHWroutes","imports")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign uniqueSet = []>
// always imported
import { RouterModule } from "@angular/router";
import { NgModule } from '@angular/core';
import { CommonModule } from "@angular/common"

import { routes } from '${pathHWroutes}';
// imports of pages in the package
<#list 0..(astguiCompilationUnitList?size - 1) as index>
import { ${astguiCompilationUnitList[index].getComponentName()}Component } from "${paths[index]}";
</#list>
<#if existHWCModules >
import { config } from '${pathHWModule}';
</#if>




<#list imports?keys as key>
    import {${key}} from "${imports[key]}";
    <#assign uniqueSet=uniqueSet+[key]>
</#list>


@NgModule({
declarations: [
<#list astguiCompilationUnitList as ast>
    ${ast.getComponentName()}Component,
</#list>
<#if existHWCModules >
   ...config.declarations
</#if>
],
imports: [
    RouterModule.forChild(routes),
    CommonModule,
<#list uniqueSet as moduleClassName>
    ${moduleClassName},
</#list>
<#if existHWCModules >
  ...config.imports
</#if>
],
exports: [
<#list astguiCompilationUnitList as ast>
    ${ast.getComponentName()}Component,
</#list>
<#if existHWCModules >
  ...config.exports
</#if>
]
})

export class ${compDecl}_Module {}





