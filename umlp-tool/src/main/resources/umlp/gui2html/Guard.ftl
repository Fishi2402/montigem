<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
${tc.signature("args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign thenStatement = ast.getThenStatement().getGUIBlockContentList()>
<#assign elseTpl = ast.getId() + "Else">

<ng-template [ngIf]='${genHelper.print(ast.getCondition())}'
  <#if ast.isPresentElseStatement()>[ngIfElse]='${elseTpl}'</#if>
>
  <#list theStatement as content>
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
