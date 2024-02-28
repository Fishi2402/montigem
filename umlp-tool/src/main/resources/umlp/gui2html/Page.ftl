<#-- (c) https://github.com/MontiCore/monticore -->

<#assign genHelper = glex.getGlobalVar("genHelper")>
<#if ast.getGUIComponentDeclaration().isPresentGUIBlock()>
  <#assign contentList = ast.getGUIComponentDeclaration().getGUIBlock().getGUIBlockContentList()>
  <#list contentList as content>
    ${tc.include("umlp.gui2html.BlockContentOrArgValue", content)}
  </#list>
</#if>
