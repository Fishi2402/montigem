<#-- (c) https://github.com/MontiCore/monticore -->

<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign htmlHelper = glex.getGlobalVar("htmlHelper")>

<#if ast.getGUIComponentDeclaration().isPresentGUIBlock()>
  ${tc.include("umlp.gui2html.Block", ast.getGUIComponentDeclaration().getGUIBlock())}
</#if>
