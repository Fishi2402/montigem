<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDInterface ast -->

export interface ${ast.getName()} <#rt><#lt>
<#if ast.isPresentCDExtendUsage()>extends ${cdPrinter.printObjectTypeList(ast.getCDExtendUsage().getSuperclassList())} </#if> { <#rt><#lt>
  ${tc.include("umlp.gui2ts.TSInterfaceBody", ast)}
}

export function ${ast.getName()}Mixin<T extends new (...args: any[]) => {}>(base: T = class {} as T) {
  return class extends base implements ${ast.getName()} {
    ${tc.include("umlp.gui2ts.TSClassBody", ast)}
  }
}
