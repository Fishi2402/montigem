<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a module ts file from the given model -->
${tc.signature("modules", "componentPath")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign viewComponents = glex.getGlobalVar("viewComponents")>
<#assign compDecl = ast.getGUIComponentDeclaration()>

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { routes } from "./${compDecl.getName()}Routing";
import { ${compDecl.getName()} } from "${componentPath}";
<#list ast.getMCImportStatementList() as imp>
  <#if genHelper.isGuiImport(imp)>
    import { ${imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1)}Module } from "${genHelper.java2tsModule(imp.getQName())}";
  </#if>
</#list>

@NgModule({
  declarations: [
    ${compDecl.getName()}
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
<#list ast.getMCImportStatementList() as imp>
  <#if genHelper.isGuiImport(imp)>
    ${imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1)}Module },
  </#if>
</#list>
  ],
  exports: [
    ${compDecl.getName()}
  ]
})

export class ${compDecl.getName()}Module { }
