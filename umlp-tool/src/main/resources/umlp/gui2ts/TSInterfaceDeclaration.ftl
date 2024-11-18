<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDInterface ast -->

export interface ${ast.getName()} <#rt><#lt>
<#if ast.isPresentCDExtendUsage()>extends ${cdPrinter.printObjectTypeList(ast.getCDExtendUsage().getSuperclassList())} </#if> { <#rt><#lt>
  ${tc.include("umlp.gui2ts.TSInterfaceBody", ast)}
}
