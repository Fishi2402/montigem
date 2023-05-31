<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "originalclassname", "returntype", "definition", "fieldName")}
${cd4c.method("public ListResult<${returntype}, List<${returntype}>> doRun()")}

Optional<${originalclassname}> ${originalclassname?uncap_first} = ${definition}Manager.${originalclassname?uncap_first}Opt(gemId);
if(${originalclassname?uncap_first}.isPresent()){
    return ListResult.of(getId(), ${originalclassname?uncap_first}.get().get${fieldName?cap_first}List(), "${returntype}");
} else {
    return ListResult.error(getId(), "${returntype}");
}
