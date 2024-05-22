<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("htmlPath", "scssPaths", "selector","ast","hwcPath","generateAnnotation")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";

<#list ast.getMCImportStatementList() as imp>
  <#if genHelper.isGuiImport(imp)>
    <#if genHelper.isTSPackageImport(imp.getQName())>
      <#if genHelper.existsHandwrittenClass(hwcPath,imp.getQName())>
        import { ${imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1)} }from "${genHelper.getComponentPath(imp.getQName())}";
      </#if>
      <#if !genHelper.existsHandwrittenClass(hwcPath,imp.getQName())>
        import { ${imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1)}Component }from "${genHelper.getComponentPath(imp.getQName())}Component";
      </#if>
    </#if>
    <#if !genHelper.isTSPackageImport(imp.getQName())>
      import { ${genHelper.getModuleClassName(imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1))} } from "${genHelper.java2tsModule(imp.getQName())}";
    </#if>
  </#if>
</#list>

<#if glex.getGlobalVar("UMLPGeneratorConfig").useJsweet>
  import { UmlpToAsyncPipe } from "@umlp/commonj2ts";
</#if>


export const config = {
imports: [    CommonModule,
<#list ast.getMCImportStatementList() as imp>
  <#if genHelper.isGuiImport(imp)>
    <#if genHelper.isTSPackageImport(imp.getQName())>
      <#if genHelper.existsHandwrittenClass(hwcPath,imp.getQName())>
        ${imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1)},
      </#if>
      <#if !genHelper.existsHandwrittenClass(hwcPath,imp.getQName())>
        ${imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1)}Component,
      </#if>
    </#if>
    <#if !genHelper.isTSPackageImport(imp.getQName())>
      ${genHelper.getModuleClassName(imp.getMCQualifiedName().getParts(imp.getMCQualifiedName().sizeParts() - 1))},
    </#if>
  </#if>
</#list>

<#if glex.getGlobalVar("UMLPGeneratorConfig").useJsweet>
  UmlpToAsyncPipe,
</#if>
],

selector:'${selector}',
templateUrl:'${htmlPath}',
styles: [":host{ display: flex; flex-direction: column }"],
styleUrls: [<#list scssPaths as path>'${path}',</#list>]
}

<#if generateAnnotation>
@Component({
standalone: true,
imports: [
... config.imports,
],
selector: config.selector,
templateUrl: config.templateUrl,
styles: config.styles,
styleUrls: config.styleUrls
})
</#if>
<#if !generateAnnotation>
@Directive()
</#if>
