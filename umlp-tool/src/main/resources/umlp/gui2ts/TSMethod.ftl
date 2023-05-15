<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("cd2java.Annotations")}
<#-- Annotations hook -->
${defineHookPoint("MethodContent:Annotations")}
<#assign isAbstract = ast.getModifier().isAbstract()>
<#-- TODO AGe: wildcard generics -->
<#if cdPrinter.isWildCardGeneric(ast.getMCReturnType())>
${cdPrinter.printSimpleModifier(ast.getModifier())} ${ast.getName()}<${cdPrinter.printType(ast.getMCReturnType())}>(${cdPrinter.printCDParametersDecl(ast.getCDParameterList())}): T
<#else>
${cdPrinter.printSimpleModifier(ast.getModifier())} ${ast.getName()} (${cdPrinter.printCDParametersDecl(ast.getCDParameterList())}): ${cdPrinter.printType(ast.getMCReturnType())}
</#if>
<#if ast.isPresentCDThrowsDeclaration()> ${cdPrinter.printThrowsDecl(ast.getCDThrowsDeclaration())}</#if>
<#if isAbstract>;<#else> {
    ${tc.include("cd2java.EmptyBody")}
}
</#if>
