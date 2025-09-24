<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "fieldTypeCdName", "initializationTypeName", "fieldName", "fieldType", "isAssoc")}
<#if isAssoc>
${cd4c.method("public void ${initializationTypeName?uncap_first}Remove${fieldName?cap_first} (long id, long __val)")}
<#else>
${cd4c.method("public void ${initializationTypeName?uncap_first}Remove${fieldName?cap_first} (long id, ${fieldType} __val)")}
</#if>
  Optional<${initializationTypeName}> ${initializationTypeName?uncap_first} = ${cdName}Manager.${initializationTypeName?uncap_first}Opt(id);
<#if isAssoc>
  Optional<${fieldType}> ${fieldName?uncap_first} = ${fieldTypeCdName}Manager.${fieldType?uncap_first}Opt(__val);
  if (!${initializationTypeName?uncap_first}.isPresent()) {
    Log.warn("Could not receive ${initializationTypeName?uncap_first} with id " + id + " from server");
  } else if (!${fieldName?uncap_first}.isPresent()) {
    Log.warn("Could not receive ${fieldType?uncap_first} with id " + __val + " from server");
  } else {
    ${initializationTypeName?uncap_first}.get().remove${fieldName?cap_first}(${fieldName?uncap_first}.get());
    Log.info("Successful remove of attribute", UMLP_SHELL_API);
  }
<#else>
  if (!${initializationTypeName?uncap_first}.isPresent()) {
    Log.warn("Could not receive ${initializationTypeName?uncap_first} with id " + id + " from server");
  } else {
    ${initializationTypeName?uncap_first}.get().remove${fieldName?cap_first}(__val);
    Log.info("Successful remove of attribute", UMLP_SHELL_API);
  }
</#if>
