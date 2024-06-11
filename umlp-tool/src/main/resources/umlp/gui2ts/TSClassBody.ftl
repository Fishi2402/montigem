<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDClass ast -->

<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

<#list ast.getCDAttributeList() as attribute>
  ${tc.include("umlp.gui2ts.TSAttribute", attribute)}
  ${tc.include("umlp.Empty", attribute)}
</#list>

${tc.include("umlp.gui2ts.TSConstructor")}

<#list ast.getCDMethodList() as method>
  ${tc.include("umlp.gui2ts.TSMethod", method)}
</#list>