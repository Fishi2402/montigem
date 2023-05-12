<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "originalclassname", "returntype", "definition", "fieldName")}
${cd4c.method("public ListResult<${returntype}, List<${returntype}>> doRun()")}

let ${originalclassname?uncap_first}: ${originalclassname} = ${definition}Manager.${originalclassname?uncap_first}Opt(this.gemId);
if(${originalclassname?uncap_first} != null){
    return ListResult.of(this.getId(), ${originalclassname?uncap_first}.get${fieldName?cap_first}(), "${returntype}");
} else {
    return ListResult.error(this.getId(), "${returntype}");
}