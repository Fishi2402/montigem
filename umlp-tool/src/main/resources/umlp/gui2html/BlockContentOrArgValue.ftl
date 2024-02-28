<#-- (c) https://github.com/MontiCore/monticore -->

<#assign genHelper = glex.getGlobalVar("genHelper")>

<#if ast.isPresentExpression()>
  ${tc.include("umlp.gui2html.Expression", ast.getExpression())}
<#elseif ast.isPresentGUIComponent()>
  ${tc.include("umlp.gui2html.Component", ast.getGUIComponent())}
<#elseif ast.isPresentGUIGuard()>
  ${tc.include("umlp.gui2html.Guard", ast.getGUIGuard())}
<#elseif ast.isPresentGUIIterate()>
  ${tc.include("umlp.gui2html.Iterate", ast.getGUIIterate())}
</#if>

