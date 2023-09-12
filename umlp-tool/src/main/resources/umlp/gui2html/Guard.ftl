<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
${tc.signature("args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign thenStatement = ast.getThenStatement().getGUIBlockContentList()>
<#assign elseTpl = genHelper.getNgTemplateActualName(ast.getId().getName() + "Else")>
<#assign isTpl = genHelper.isNgTemplate(ast.getId().getName())>

<#if isTpl><ng-template #${genHelper.getNgTemplateActualName(ast.getId().getName())}></#if>
<ng-template [ngIf]='${genHelper.gui2cdAttrGetterName(ast.getId().getName(), "condition")}(<#list args as arg> ${arg}<#sep>, </#list>)'
  <#if ast.isPresentElseStatement()>[ngIfElse]='${elseTpl}'</#if>
>
  <#list thenStatement as content>
    ${tc.includeArgs("umlp.gui2html.BlockContentOrArgValue", content, [args])}
  </#list>
</ng-template>

<#if ast.isPresentElseStatement()>
  <ng-template #${elseTpl}>
    <#assign elseStatement = ast.getElseStatement().getGUIBlockContentList()>
    <#list elseStatement as content>
      ${tc.includeArgs("umlp.gui2html.BlockContentOrArgValue", content, [args])}
    </#list>
  </ng-template>
</#if>
<#if isTpl></ng-template></#if>
