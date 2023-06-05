<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
${tc.signature("args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#-- TODO AGe: uncomment when problem with RangeControl is resolved, remove itemName and iterable
<#assign itemName = genHelper.getIteratorItemName(ast)>
<#assign iterable = genHelper.getIteratorIterable(ast)>
-->
<#assign itemName = genHelper.getShortIteratorItemName(ast)>
<#assign iterable = genHelper.getShortIteratorIterable(ast)>
<#assign counterName = genHelper.getIteratorCounterName(ast)>
<#assign contentList = ast.getGUIBlock().getGUIBlockContentList()>
<#assign isTpl = genHelper.isNgTemplate(ast.getId().getName())>

<#-- TODO AGe: uncomment when problem with RangeControl is resolved
<ng-container *ngFor='let ${itemName} of ${iterable}; index as ${counterName}'>
-->
<#assign args = args + [itemName]>
<#if isTpl><ng-template #${genHelper.getNgTemplateActualName(ast.getId().getName())}></#if>
<ng-container *ngFor='let ${itemName} of ${iterable}; index as ${counterName}'>
  <#list contentList as content>
    ${tc.includeArgs("umlp.gui2html.BlockContentOrArgValue", content, [args])}
  </#list>
</ng-container>
<#if isTpl></ng-template></#if>
<#assign index = args?size - 2>
<#if index gt 0><#assign args = args[0..index]></#if>
