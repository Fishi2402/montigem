<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
${tc.signature("args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign thenStatement = ast.getThenStatement().getGUIBlockContentList()>
<#assign elseTpl = genHelper.getNgTemplateActualName(ast.getId().getName() + "Else")>
<#assign isTpl = genHelper.isNgTemplate(ast.getId().getName())>

<ng-template [ngIf]='${genHelper.gui2cdAttrGetterName(ast.getId().getName(), "condition")}()'
  <#if ast.isPresentElseStatement()>[ngIfElse]='${elseTpl}'</#if>
  <#if isTpl>#${genHelper.getNgTemplateActualName(ast.getId().getName())}</#if>
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
