<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "originalclassname", "returntype", "definition", "fieldName")}
${cd4c.method("public Promise<ListResult<${returntype}, List<${returntype}>>> doRun()")}

return ${definition}.${originalclassname?uncap_first}Opt(this.gemId).then(v => {
  let ${originalclassname?uncap_first}: ${originalclassname} = v;
  if (${originalclassname?uncap_first} != null) {
    return ListResult.of(this.getId(), ${originalclassname?uncap_first}.get${fieldName?cap_first}List(), "${returntype}");
  } else {
    return ListResult.error(this.getId(), "${returntype}");
  }
});
