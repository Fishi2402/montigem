<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

<#if ast.isPresentExpression()>
  <ng-container *ngTemplateOutlet="${genHelper.print(ast.getExpression())}"></ng-container>
  ${tc.includeArgs("umlp.gui2html.Expression", ast.getExpression(), [genHelper.getNameOfNameExpression(ast.getExpression())])}
<#elseif ast.isPresentGUIComponent()>
  ${tc.includeArgs("umlp.gui2html.Component", ast.getGUIComponent(), [args])}
<#elseif ast.isPresentGUIGuard()>
  ${tc.includeArgs("umlp.gui2html.Guard", ast.getGUIGuard(), [args])}
<#elseif ast.isPresentGUIIterate()>
  ${tc.includeArgs("umlp.gui2html.Iterate", ast.getGUIIterate(), [args])}
</#if>

