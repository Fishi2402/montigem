<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a html file from the given model -->
${tc.signature("args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

<#if ast.isPresentExpression()>
  <#-- TODO AGe -->
  ${genHelper.print(ast.getExpression())}
<#elseif ast.isPresentGUIComponent()>
  ${tc.includeArgs("umlp.gui2html.Component", ast.getGUIComponent(), [args])}
<#elseif ast.isPresentGUIGuard()>
  ${tc.includeArgs("umlp.gui2html.Guard", ast.getGUIGuard(), [args])}
<#elseif ast.isPresentGUIIterate()>
  ${tc.includeArgs("umlp.gui2html.Iterate", ast.getGUIIterate(), [args])}
</#if>
