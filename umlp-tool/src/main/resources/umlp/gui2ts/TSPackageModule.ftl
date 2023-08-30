<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("astguiCompilationUnitList","ids","compDecl","topNameMap","paths")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign uniqueSet = []>
// always imported
import { RouterModule } from "@angular/router";
import { NgModule } from '@angular/core';
import { routes } from './${compDecl}.Routing';
// imports of pages in the package
<#list 0..(astguiCompilationUnitList?size - 1) as index>
  import { ${astguiCompilationUnitList[index].getComponentName()}Component } from "${paths[index]}";
</#list>


<#list astguiCompilationUnitList as ast>
  <#list ast.getMCImportStatementList() as imp>
      <#assign moduleClassName =genHelper.getModuleClassName(imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1)) >
    <#if genHelper.isGuiImport(imp) && !uniqueSet?seq_contains(moduleClassName) && !genHelper.isCreatedTSComponent(moduleClassName) >
      import { ${moduleClassName} } from "${genHelper.java2tsModule(imp.getQName())}";
        <#assign uniqueSet = uniqueSet + [moduleClassName]>
    </#if>
      <#if genHelper.isGuiImport(imp) &&
      !uniqueSet?seq_contains(moduleClassName) &&
      genHelper.isCreatedTSComponent(moduleClassName) &&
      compDecl+"Module"!=genHelper.getPackageModuleName(imp) &&
      !uniqueSet?seq_contains(genHelper.getPackageModuleName(imp)) >
        import { ${genHelper.getPackageModuleName(imp)} } from "${genHelper.getPackageModulePath(imp)}";
          <#assign uniqueSet = uniqueSet + [genHelper.getPackageModuleName(imp)]>
      </#if>
  </#list>
</#list>


@NgModule({
declarations: [
<#list astguiCompilationUnitList as ast>
    ${ast.getComponentName()}Component,
</#list>
],
imports: [
RouterModule.forChild(routes),
<#list uniqueSet as moduleClassName>
  ${moduleClassName},
</#list>
],
exports: [
<#list astguiCompilationUnitList as ast>
    ${ast.getComponentName()}Component,
</#list>
]
})

export class ${compDecl}Module {}





