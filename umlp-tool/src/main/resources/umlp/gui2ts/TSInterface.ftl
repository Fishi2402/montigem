<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("package")}
/* (c) https://github.com/MontiCore/monticore */
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${defineHookPoint("InterfaceContent:addComment")}

${cdPrinter.printImportList(cd4c.getImportList(ast))}

<#-- Imports hook -->
${defineHookPoint("InterfaceContent:Imports")}

${tc.include("cd2java.Annotations")}

<#-- Annotations hook -->
${defineHookPoint("InterfaceContent:Annotations")}

export interface ${ast.getName()} <#rt><#lt>
<#if ast.isPresentCDExtendUsage()>extends ${cdPrinter.printObjectTypeList(ast.getCDExtendUsage().getSuperclassList())} </#if> { <#rt><#lt>

<#-- Elements HOOK -->
${defineHookPoint("InterfaceContent:Elements")}

<#list ast.getCDAttributeList() as attribute>
    ${tc.include("umlp.gui2ts.TSAttribute", attribute)}
</#list>

<#list ast.getCDMethodList() as method>
    ${tc.include("umlp.gui2ts.TSInterfaceMethod", method)}
</#list>
}
