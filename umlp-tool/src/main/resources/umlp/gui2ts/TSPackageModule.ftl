<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("astguiCompilationUnitList","ids","compDecl","topNameMap","paths")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign uniqueSet = []>
// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
<#list 0..(astguiCompilationUnitList?size - 1) as index>
  import { ${astguiCompilationUnitList[index].getComponentName()} } from "${paths[index]}";
</#list>


<#list astguiCompilationUnitList as ast>
  <#list ast.getMCImportStatementList() as imp>
      <#assign moduleClassName =genHelper.getModuleClassName(imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1)) >
    <#if genHelper.isGuiImport(imp) && !uniqueSet?seq_contains(moduleClassName) >
      import { ${moduleClassName} } from "${genHelper.java2tsModule(imp.getQName())}";
        <#assign uniqueSet = uniqueSet + [moduleClassName]>
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

<#list uniqueSet as moduleClassName>
  ${moduleClassName},
</#list>
],
exports: [
]
})






