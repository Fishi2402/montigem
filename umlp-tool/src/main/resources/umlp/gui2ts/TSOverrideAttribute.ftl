<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("umlp.gui2ts.TSAnnotations")}
<#-- Annotations hook -->
${defineHookPoint("AttributeContent:Annotations")}
${cdPrinter.printSimpleModifier(ast.getModifier())} override ${ast.getName()}: ${cdPrinter.printType(ast.getMCType())}
<#if ast.isPresentInitial()>
    <#t> = ${cdPrinter.printExpression(ast.getInitial())}
<#else>
    ${tc.include("cd2java.Value")}
</#if>
<#lt>;
