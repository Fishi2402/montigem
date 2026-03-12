<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("package")}
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="ast" type="de.monticore.cd4codebasis._ast.ASTCDClass" -->
${glex.setGlobalValue("cdPrinter", tc.instantiate("umlp.py.CdPyUtilsPrinter"))}
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
# (c) https://github.com/MontiCore/monticore */

<#include "cd2python.PythonMacros.ftl"> <#--Load macros-->


<#--  Capture the output of the inner macro-->
<#assign output>

  ${defineHookPoint("ClassContent:addComment")}


<#--${tc.include("cd2java.Imports")}-->
${cdPrinter.printImportList(cd4c.getImportList(ast))}

<#-- Imports hook -->
  ${defineHookPoint("ClassContent:Imports")}

  ${tc.include("cd2java.JavaDoc")}

  ${tc.include("cd2java.Annotations")}


  ${defineHookPoint("ClassContent:Annotations")}

  class ${ast.getName()}  <#rt><#lt>
  <#if ast.isPresentCDExtendUsage()>( <#list ast.getCDExtendUsage().getSuperclassList() as sc> ${cdPrinter.printType(sc)}<#sep>,</#list> ) </#if> <#rt><#lt>
  : <#rt><#lt>

  <@indent>
  <#-- Elements HOOK -->
    ${defineHookPoint("ClassContent:Elements")}

    <#list ast.getCDAttributeList() as attribute>
      ${tc.include("cd2python.Attribute", attribute)}
    </#list>

  <#--  <#list ast.getCDConstructorList() as constructor>-->
  <#--    ${tc.include("cd2java.Constructor", constructor)}-->
  <#--  </#list>-->

    <#list ast.getCDMethodList() as method>
      ${tc.include("cd2python.Method", method)}
    </#list>

    ${defineHookPoint("cd2python.Class::postBody")}

  </@indent>


</#assign>

#from __future__ import annotations
import typing
import pydantic
import montigem_runtime
<#--from montigem_runtime.gembase  import GemBase-->
<#--  And then add real indentation-->
${cdPrinter.unwrapIndentation(output)}
<#-- TODO: change CDGenerator JAVA_EXTENSION/path-->
<#--Expose path to setup-->
