<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

export <#if cdPrinter.isAbstract(ast)>abstract</#if> class ${tc.include("umlp.gui2ts.TSClassName", ast)} <#rt><#lt>
<#if ast.isPresentTypeParameters()>
${tc.include("umlp.gui2ts.TSClassTypeParameters", ast)}
</#if>
${tc.include("umlp.gui2ts.TSClassExtends", ast)}
${tc.include("umlp.gui2ts.TSClassImplements", ast)} {
  ${tc.include("umlp.gui2ts.TSClassBody", ast)}
}
