<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDInterface ast -->

<#--
export interface ${ast.getName()} <#t>
<#if ast.isPresentCDExtendUsage()>extends ${cdPrinter.printObjectTypeList(ast.getCDExtendUsage().getSuperclassList())} </#if> { <#t>
  ${tc.include("umlp.gui2ts.TSInterfaceBody", ast)}
}
-->

export function ${ast.getName()}<T extends new (...args: any[]) => {}>(base: T = class {} as T) {
  return class extends base implements
    <#if ast.isPresentCDExtendUsage()> ${cdPrinter.printObjectTypeList(ast.getCDExtendUsage().getSuperclassList())} </#if> { <#t>
    ${tc.include("umlp.gui2ts.TSClassBody", ast)}
  }
}
