<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
<#assign genHelper = glex.getGlobalVar("genHelper")>

<#if ast.isPresentExpression()>
  <#-- TODO AGe -->
  ${genHelper.print(ast.getExpression())}
<#elseif ast.isPresentGUIComponent()>
  ${tc.include("umlp.gui2html.Component", ast.getGUIComponent())}
<#elseif ast.isPresentGUIGuard()>
  ${tc.include("umlp.gui2html.Guard", ast.getGUIGuard())}
<#elseif ast.isPresentGUIIterate()>
  ${tc.include("umlp.gui2html.Iterate", ast.getGUIIterate())}
</#if>
