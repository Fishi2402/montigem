<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->

<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
${defineHookPoint("ClassContent:Elements")}

<#list ast.getCDAttributeList() as attribute>
  ${tc.include("umlp.gui2ts.TSAttribute", attribute)}
</#list>

${tc.include("umlp.gui2ts.TSConstructor")}

<#list ast.getCDMethodList() as method>
  ${tc.include("umlp.gui2ts.TSMethod", method)}
</#list>