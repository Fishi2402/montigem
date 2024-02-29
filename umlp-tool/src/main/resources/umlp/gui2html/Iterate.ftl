<#-- (c) https://github.com/MontiCore/monticore -->

<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign htmlHelper = glex.getGlobalVar("htmlHelper")>
<#assign isNgTemplate = htmlHelper.isNgTemplate(ast)>
<#assign extraArgs = genHelper.findIterationItemSymbols(ast.getEnclosingScope())>
<#assign tplName = genHelper.getNgTemplateName(ast.getId().getName())>

<#assign itemName = genHelper.getShortIteratorItemName(ast)>
<#assign iterable = genHelper.getShortIteratorIterable(ast)>
<#assign counterName = genHelper.getIteratorCounterName(ast)>
<#assign contentList = ast.getGUIBlock().getGUIBlockContentList()>

<#if isNgTemplate>
  <ng-template #${tplName}>
</#if>
<ng-container *ngFor='let ${itemName} of ${iterable}; index as ${counterName}'>
  <#list contentList as content>
    ${tc.include("umlp.gui2html.BlockContentOrArgValue", content)}
  </#list>
</ng-container>
<#if isNgTemplate>
  </ng-template>
</#if>
