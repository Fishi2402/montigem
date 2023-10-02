<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "originalclassname", "returntype", "managerName", "fieldName", "prefix")}
${cd4c.method("public SimpleResult<${returntype}> doAction()")}

Optional<${originalclassname}> ${originalclassname?uncap_first} = ${managerName}.${originalclassname?uncap_first}Opt(gemId);
if(${originalclassname?uncap_first}.isPresent()){
    return new SimpleResult(getId(), ${originalclassname?uncap_first}.get().${prefix}${fieldName?cap_first}(), "${returntype}");
} else {
    return new SimpleResult(getId(), LoadingState.EMPTY, "${returntype}");
}
