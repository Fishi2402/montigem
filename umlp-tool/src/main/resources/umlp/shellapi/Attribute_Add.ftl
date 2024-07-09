<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName", "fieldType", "isAssoc")}
<#if isAssoc>
${cd4c.method("public void ${className?uncap_first}Add${fieldName?cap_first} (long id, long __val)")}
<#else>
${cd4c.method("public void ${className?uncap_first}Add${fieldName?cap_first} (long id, ${fieldType} __val)")}
</#if>
  Optional<${className}> ${className?uncap_first} = ${cdName}Manager.${className?uncap_first}Opt(id);
<#if isAssoc>
  Optional<${fieldType}> ${fieldName?uncap_first} = ${cdName}Manager.${fieldType?uncap_first}Opt(__val);
  if (!${className?uncap_first}.isPresent()) {
    Log.warn("Could not receive ${className?uncap_first} with id " + id + " from server");
  } else if (!${fieldName?uncap_first}.isPresent()) {
    Log.warn("Could not receive ${fieldType?uncap_first} with id " + __val + " from server");
  } else {
    ${className?uncap_first}.get().add${fieldName?cap_first}(${fieldName?uncap_first}.get());
    Log.info("Successful add of attribute", UMLP_SHELL_API);
  }
<#else>
  if (!${className?uncap_first}.isPresent()) {
    Log.warn("Could not receive ${className?uncap_first} with id " + id + " from server");
  } else {
    ${className?uncap_first}.get().add${fieldName?cap_first}(__val);
    Log.info("Successful add of attribute", UMLP_SHELL_API);
  }
</#if>
