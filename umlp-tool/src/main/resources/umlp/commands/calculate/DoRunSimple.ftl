<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "originalclassname", "returntype", "managerName", "fieldName")}
${cd4c.method("public SimpleResult<${returntype}> doRun()")}

Optional<${originalclassname}> ${originalclassname?uncap_first} = ${managerName}.${originalclassname?uncap_first}Opt(gemId);
if(${originalclassname?uncap_first}.isPresent()){
    return new SimpleResult(getId(), ${originalclassname?uncap_first}.get().get${fieldName?cap_first}(), "${returntype}");
} else {
    return new SimpleResult(getId(), LoadingState.EMPTY, "${returntype}");
}
