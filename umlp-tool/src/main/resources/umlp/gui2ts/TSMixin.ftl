<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->

export function ${ast.getName()}<T extends new (...args: any[]) => {}>(base: T = class {} as T) {
  return class extends base <#if ast.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(ast.getCDInterfaceUsage().getInterfaceList())} </#if>{
    ${tc.include("umlp.gui2ts.TSClassBody", ast)}
  }
}
