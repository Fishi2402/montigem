<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("cd2java.Annotations")}
<#-- Annotations hook -->
${defineHookPoint("MethodContent:Annotations")}
<#assign isAbstract = ast.getModifier().isAbstract()>
<#assign isDerived = ast.getModifier().isDerived()>
<#-- TODO AGe: wildcard generics -->
<#if cdPrinter.isWildCardGeneric(ast.getMCReturnType())>
<#if !isDerived>${cdPrinter.printSimpleModifier(ast.getModifier())}</#if> ${ast.getName()}<${cdPrinter.printType(ast.getMCReturnType())}>(${cdPrinter.printCDParametersDecl(ast.getCDParameterList())}): T
<#else>
<#if !isDerived>${cdPrinter.printSimpleModifier(ast.getModifier())}</#if> ${ast.getName()} (${cdPrinter.printCDParametersDecl(ast.getCDParameterList())}): ${cdPrinter.printType(ast.getMCReturnType())}
</#if>
<#if ast.isPresentCDThrowsDeclaration()> ${cdPrinter.printThrowsDecl(ast.getCDThrowsDeclaration())}</#if>
<#-- TODO AGe: what actually happens on get derived? -->
<#if isAbstract>;<#elseif isDerived>{return null;}<#else> {
    ${tc.include("cd2java.EmptyBody")}
}
</#if>
