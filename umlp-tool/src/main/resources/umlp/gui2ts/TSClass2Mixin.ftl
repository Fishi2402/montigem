<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->
<#assign tmp = "Abstract" + ast.getName()>

export function ${ast.getName()}<T extends abstract new (...args: any[]) => {}>(base: T) {
  abstract class ${tmp} extends base
    <#if ast.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(ast.getCDInterfaceUsage().getInterfaceList())} </#if> { <#t>
    ${tc.include("umlp.gui2ts.TSClassBody", ast)}
  }

  return ${tmp};
}
