<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->

export <#if cdPrinter.isAbstract(ast)>abstract</#if> class ${ast.getName()} <#rt><#lt>
${tc.include("umlp.gui2ts.TSClassExtends", ast)}
<#if ast.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(ast.getCDInterfaceUsage().getInterfaceList())} </#if>{
  ${tc.include("umlp.gui2ts.TSClassBody", ast)}
}
