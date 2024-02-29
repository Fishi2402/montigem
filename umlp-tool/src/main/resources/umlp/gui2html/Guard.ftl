<#-- (c) https://github.com/MontiCore/monticore -->

<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign extraArgs = genHelper.findIterationItemSymbols(ast.getEnclosingScope())>
<#assign elseTpl = genHelper.getNgTemplateElseBlockName(ast.getId().getName())>

<ng-template [ngIf]='${genHelper.gui2cdAttrGetterName(ast.getId().getName(), "condition")}(<#list extraArgs as arg> ${arg.getName()}<#sep>, </#list>)'
  <#if ast.isPresentElseStatement()>[ngIfElse]='${elseTpl}'</#if>
>
  ${tc.include("umlp.gui2html.Block", ast.getThenStatement())}
</ng-template>

<#if ast.isPresentElseStatement()>
  <ng-template #${elseTpl}>
    ${tc.include("umlp.gui2html.Block", ast.getElseStatement())}
  </ng-template>
</#if>
