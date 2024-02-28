<#-- (c) https://github.com/MontiCore/monticore -->

<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign htmlHelper = glex.getGlobalVar("htmlHelper")>
<#assign isNgTemplate = htmlHelper.isNgTemplate(ast)>
<#assign extraArgs = genHelper.getIterateItems(ast)>
<#assign tplName = genHelper.getNgTemplateName(ast.getId().getName())>

<#assign thenStatement = ast.getThenStatement().getGUIBlockContentList()>
<#assign elseTpl = genHelper.getNgTemplateElseBlockName(ast.getId().getName())>

<#if isNgTemplate>
  <ng-template #${tplName}>
</#if>
<ng-template [ngIf]='${genHelper.gui2cdAttrGetterName(ast.getId().getName(), "condition")}(<#list extraArgs as arg> ${arg}<#sep>, </#list>)'
  <#if ast.isPresentElseStatement()>[ngIfElse]='${elseTpl}'</#if>
>
  <#list thenStatement as content>
    ${tc.include("umlp.gui2html.BlockContentOrArgValue", content)}
  </#list>
</ng-template>

<#if ast.isPresentElseStatement()>
  <ng-template #${elseTpl}>
    <#assign elseStatement = ast.getElseStatement().getGUIBlockContentList()>
    <#list elseStatement as content>
      ${tc.include("umlp.gui2html.BlockContentOrArgValue", content)}
    </#list>
  </ng-template>
</#if>
<#if isNgTemplate>
  </ng-template>
</#if>
