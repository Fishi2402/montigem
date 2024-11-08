<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTCDInterface ast -->
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

<#-- Elements HOOK -->
${defineHookPoint("InterfaceContent:Elements")}

<#list ast.getCDAttributeList() as attribute>
    ${tc.include("umlp.gui2ts.TSAttribute", attribute)}
</#list>

<#list ast.getCDMethodList() as method>
    ${tc.include("umlp.gui2ts.TSInterfaceMethod", method)}
</#list>