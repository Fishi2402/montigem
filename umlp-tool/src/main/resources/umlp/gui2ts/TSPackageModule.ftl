<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("astguiCompilationUnitList","ids","compDecl","topNameMap","paths")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
<#list 0..(astguiCompilationUnitList?size - 1) as index>
  import { ${astguiCompilationUnitList[index].getComponentName()} } from "${paths[index]}";
</#list>

<#list astguiCompilationUnitList as ast>
  <#list ast.getMCImportStatementList() as imp>
    <#if genHelper.isGuiImport(imp)>
      import { ${genHelper.getModuleClassName(imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1))} } from "${genHelper.java2tsModule(imp.getQName())}";
    </#if>
  </#list>
</#list>


@NgModule({
declarations: [
<#list astguiCompilationUnitList as ast>
    ${ast.getComponentName()},
</#list>
],
imports: [
CommonModule,
RouterModule.forChild(routes),
<#list astguiCompilationUnitList as ast>
<#list ast.getMCImportStatementList() as imp>
  <#if genHelper.isGuiImport(imp)>
    ${genHelper.getModuleClassName(imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1))},
  </#if>
</#list>
</#list>
],
exports: [
]
})






