<#-- (c) https://github.com/MontiCore/monticore -->

<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->

${tc.include("cd2java.JavaDoc")}
${tc.include("cd2java.Annotations")}
<#-- Annotations hook -->
${defineHookPoint("MethodContent:Annotations")}
<#assign isAbstract = ast.getModifier().isAbstract()>
<@compress single_line=true>
  <#if cdPrinter.isAsync(ast.getModifier())>async </#if>
  def ${ast.getName()}  (self, ${cdPrinter.printCDParametersDecl(ast.getCDParameterList(), tc, glex)})
    <#if !(cdPrinter.isVoidType(ast.getMCReturnType()))>
  -> ${cdPrinter.printType(ast.getMCReturnType())}
  </#if>
  :

<#--  <#if ast.isPresentTypeParameters()>${cdPrinter.printTypeParameters(ast.getTypeParameters())}</#if>-->
<#--  -->
<#-- -->
<#-- -->
<#--  <#if ast.isPresentCDThrowsDeclaration()> ${cdPrinter.printThrowsDecl(ast.getCDThrowsDeclaration())}</#if>-->
</@compress>

<@indent>
  <#if isAbstract>pass<#else>
    ${tc.include("cd2python.EmptyBody")}
  </#if>
</@indent>

