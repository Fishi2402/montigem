<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->

<#if ast.isPresentCDExtendUsage()> extends ${cdPrinter.printType(ast.getCDExtendUsage().getSuperclass(0))} </#if> <#rt><#lt>
