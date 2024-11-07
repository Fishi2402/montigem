<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

export <#if cdPrinter.isAbstract(ast)>abstract</#if> class ${ast.getName()} <#rt><#lt>
${tc.include("umlp.gui2ts.TSClassExtends", ast)}
${tc.include("umlp.gui2ts.TSClassImplements", ast)} {
  ${tc.include("umlp.gui2ts.TSClassBody", ast)}
}
