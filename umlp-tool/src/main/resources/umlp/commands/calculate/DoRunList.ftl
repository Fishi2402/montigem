<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "originalclassname", "returntype", "managerName", "fieldName", "multiCardinality")}
${cd4c.method("public ListResult<${returntype}, List<${returntype}>> doAction()")}

Optional<${originalclassname}> ${originalclassname?uncap_first} = ${managerName}.${originalclassname?uncap_first}Opt(gemId);
if(${originalclassname?uncap_first}.isPresent()){
    return ListResult.of(getId(), ${originalclassname?uncap_first}.get().get${fieldName?cap_first}<#if !multiCardinality>List</#if>(), "${returntype}");
} else {
    return ListResult.error(getId(), "${returntype}");
}
