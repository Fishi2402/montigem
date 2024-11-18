<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->

<#if ast.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(ast.getCDInterfaceUsage().getInterfaceList())} </#if>
