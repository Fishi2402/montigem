<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDInterface ast -->
<#assign tmp = "Abstract" + ast.getName()>

export function ${ast.getName()}<T extends abstract new (...args: any[]) => {}>(base: T) {
  abstract class ${tmp} extends ${defineHookPoint("TSMixinExtend:start", ast)} base ${defineHookPoint("TSMixinExtend:end", ast)}
    <#if ast.isPresentCDExtendUsage()>implements ${cdPrinter.printObjectTypeList(ast.getCDExtendUsage().getSuperclassList())} </#if> { <#t>
    ${tc.include("umlp.gui2ts.TSClassBody", ast)}
  }

  return ${tmp};
}
