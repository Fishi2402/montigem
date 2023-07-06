<#-- (c) https://github.com/MontiCore/monticore -->
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
/* (c) https://github.com/MontiCore/monticore */
${defineHookPoint("EnumContent:addComment")}

${cdPrinter.printImportList(cd4c.getImportList(ast))}

<#-- Imports hook -->
${defineHookPoint("EnumContent:Imports")}

${tc.include("umlp.gui2ts.TSAnnotations")}

<#-- Annotations hook -->
${defineHookPoint("EnumContent:Annotations")}

export enum ${ast.getName()} <#rt><#lt>
<#if ast.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(ast.getCDInterfaceUsage().getInterfaceList())} </#if>{

<#-- Elements HOOK -->
${defineHookPoint("EnumContent:Elements")}

<#list ast.getCDEnumConstantList() as enumConst>
    ${enumConst.getName()}
    <#if enumConst.isPresentArguments??>
        <#if enumConst.isPresentArguments()>
            <#t>(
            <#list enumConst.getArguments().getExpressionList() as expr>
                <#t>${cdPrinter.printExpression(expr)}<#if !expr?is_last>,</#if>
            </#list>
            <#t>)
        </#if>
    </#if>
    <#if !enumConst?is_last><#t>,</#if>
</#list>

}


