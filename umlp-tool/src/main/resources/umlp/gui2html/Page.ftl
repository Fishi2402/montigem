<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#if ast.getGUIComponentDeclaration().isPresentGUIBlock()>
  <#assign contentList = ast.getGUIComponentDeclaration().getGUIBlock().getGUIBlockContentList()>

  <#list contentList as content>
    ${tc.includeArgs("umlp.gui2html.BlockContentOrArgValue", content, [[]])}
  </#list>
</#if>
