<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("htmlPath", "scssPaths", "selector","importedComponents","hwcPath","generateAnnotation")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";

<#list importedComponents as imp>
  <#assign simpleName = genHelper.getModuleName(imp)>
  <#if genHelper.isTSPackageImport(imp)>
    <#if genHelper.existsHandwrittenClass(hwcPath,imp)>
      import { ${simpleName} }from "${genHelper.getComponentPath(imp)}";
    </#if>
    <#if !genHelper.existsHandwrittenClass(hwcPath,imp)>
      import { ${simpleName}Component }from "${genHelper.getComponentPath(imp)}Component";
    </#if>
  </#if>
  <#if !genHelper.isTSPackageImport(imp)>
    import { ${genHelper.getModuleClassName(simpleName)} } from "${genHelper.java2tsModule(imp)}";
  </#if>
</#list>

<#if glex.getGlobalVar("UMLPGeneratorConfig").useJsweet>
  import { UmlpToAsyncPipe } from "@umlp/commonj2ts";
</#if>


export const config = {
imports: [    CommonModule,
<#list importedComponents as imp>
  <#assign simpleName = genHelper.getModuleName(imp)>
  <#if genHelper.isTSPackageImport(imp)>
    <#if genHelper.existsHandwrittenClass(hwcPath,imp)>
      ${simpleName},
    </#if>
    <#if !genHelper.existsHandwrittenClass(hwcPath,imp)>
      ${simpleName}Component,
    </#if>
  </#if>
  <#if !genHelper.isTSPackageImport(imp)>
    ${genHelper.getModuleClassName(simpleName)},
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
