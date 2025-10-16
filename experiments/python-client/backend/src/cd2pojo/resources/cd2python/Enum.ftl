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
  from enum import StrEnum

<#-- Imports hook -->
  ${defineHookPoint("ClassContent:Imports")}

  ${tc.include("cd2java.JavaDoc")}

  ${tc.include("cd2java.Annotations")}


  ${defineHookPoint("ClassContent:Annotations")}

  class ${ast.getName()}  <#rt><#lt>  (StrEnum) : <#rt><#lt>

  <@indent>
    <#list ast.getCDEnumConstantList() as enumConst>
      ${enumConst.getName()} = '${enumConst.getName()}'
    </#list>

  </@indent>


</#assign>

<#--from montigem_runtime.gembase  import GemBase-->
<#--  And then add real indentation-->
${cdPrinter.unwrapIndentation(output)}
<#-- TODO: change CDGenerator JAVA_EXTENSION/path-->
<#--Expose path to setup-->
