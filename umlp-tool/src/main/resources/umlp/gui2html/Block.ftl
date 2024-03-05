<#-- (c) https://github.com/MontiCore/monticore -->

<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign htmlHelper = glex.getGlobalVar("htmlHelper")>
<#assign contentList = ast.getGUIBlockContentList()>
<#assign templateList = htmlHelper.getNgTemplates(ast)>

<#list contentList as content>
  ${tc.include("umlp.gui2html.BlockContentOrArgValue", content)}
</#list>
<#list templateList as template>
  <#assign tplName = genHelper.getNgTemplateName(template)>
  <ng-template #${tplName}>
    ${tc.include("umlp.gui2html.BlockContentOrArgValue", template)}
  </ng-template>
</#list>
