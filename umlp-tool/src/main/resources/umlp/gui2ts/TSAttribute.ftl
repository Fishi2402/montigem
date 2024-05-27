<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("umlp.gui2ts.TSAnnotations")}
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign type = cdPrinter.printType(ast.getMCType())>

<#-- Annotations hook -->
${defineHookPoint("AttributeContent:Annotations")}
${cdPrinter.printSimpleModifier(ast.getModifier())} ${ast.getName()}: ${type}
<#if ast.isPresentInitial()>
  <#t> = ${cdPrinter.printExpression(ast.getInitial())}
<#else>
  ${tc.include("cd2java.Value")}
</#if>
<#lt>;
