<#-- (c) https://github.com/MontiCore/monticore -->
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
/* (c) https://github.com/MontiCore/monticore */

${defineHookPoint("ClassContent:addComment")}

${cdPrinter.printImportList(cd4c.getImportList(ast))}

<#-- Imports hook -->
${defineHookPoint("ClassContent:Imports")}

${tc.include("umlp.gui2ts.Annotations")}

<#-- Annotations hook -->
${defineHookPoint("ClassContent:Annotations")}

export class ${ast.getName()} <#rt><#lt>
<#if ast.isPresentCDExtendUsage()>extends ${cdPrinter.printType(ast.getCDExtendUsage().getSuperclass(0))} </#if> <#rt><#lt>
<#if ast.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(ast.getCDInterfaceUsage().getInterfaceList())} </#if>{

<#-- Elements HOOK -->
${defineHookPoint("ClassContent:Elements")}

<#list ast.getCDAttributeList() as attribute>
    ${tc.include("umlp.gui2ts.Attribute", attribute)}
</#list>

<#list ast.getCDConstructorList() as constructor>
    ${tc.include("umlp.gui2ts.Constructor", constructor)}
</#list>

<#list ast.getCDMethodList() as method>
    ${tc.include("umlp.gui2ts.Method", method)}
</#list>

}


