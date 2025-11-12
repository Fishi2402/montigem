<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("cd2java.JavaDoc")}
${tc.include("cd2java.Annotations")}
<#-- Annotations hook -->
${defineHookPoint("AttributeContent:Annotations")}
${ast.getName()} : ${cdPrinter.printType(ast.getMCType())}  <#rt><#lt>
<#if ast.isPresentInitial()>
  <#t> = ${cdPrinter.printExpression(ast.getInitial())} <#rt><#lt>
<#else>
  ${tc.include("cd2java.Value")} <#rt><#lt>
</#if>
<#lt>
