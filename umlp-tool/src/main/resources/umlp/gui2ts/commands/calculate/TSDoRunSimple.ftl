<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "originalclassname", "returntype", "definition", "fieldName")}
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
${cd4c.method("public SimpleResult<${returntype}> doRun()")}

let ${originalclassname?uncap_first}: ${originalclassname} = ${definition}Manager.${originalclassname?uncap_first}Opt(this.gemId);
if (${originalclassname?uncap_first} != null) {
  return new SimpleResult<${returntype}>().SimpleResult1(this.getId(), ${originalclassname?uncap_first}.get${fieldName?cap_first}(), "${returntype}");
} else {
  return new SimpleResult<${returntype}>().SimpleResult2(this.getId(), LoadingState.EMPTY, "${returntype}");
}