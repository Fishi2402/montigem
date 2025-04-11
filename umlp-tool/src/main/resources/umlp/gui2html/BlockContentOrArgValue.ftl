<#-- (c) https://github.com/MontiCore/monticore -->
<#assign genHelper = glex.getGlobalVar("genHelper")>

<#if ast.isPresentGUIGuard()>
  ${tc.include("umlp.gui2html.Guard", ast.getGUIGuard())}
<#elseif ast.isPresentGUIIterate()>
  ${tc.include("umlp.gui2html.Iterate", ast.getGUIIterate())}
<#elseif ast.isPresentExpression()>
  <#assign component=genHelper.getAsGuiComponent(ast.getExpression())>
  <#if component.isPresent()>
    ${tc.include("umlp.gui2html.Component", component.get())}
  <#else>
    ${tc.include("umlp.gui2html.Expression", ast.getExpression())}
  </#if>
</#if>

