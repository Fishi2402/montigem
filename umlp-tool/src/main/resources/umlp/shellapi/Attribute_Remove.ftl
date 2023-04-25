<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName", "fieldType", "isAssoc")}
<#if isAssoc>
${cd4c.method("public void ${className?uncap_first}Remove${fieldName?cap_first} (long id, long value)")}
<#else>
${cd4c.method("public void ${className?uncap_first}Remove${fieldName?cap_first} (long id, ${fieldType} value)")}
</#if>
  Optional<${className}> ${className?uncap_first} = ${cdName}Manager.${className?uncap_first}Opt(id);
<#if isAssoc>
  Optional<${fieldType}> ${fieldName?uncap_first} = ${cdName}Manager.${fieldType?uncap_first}Opt(value);
  if (!${className?uncap_first}.isPresent()) {
    Log.warn("Could not receive ${className?uncap_first} with id " + id + " from server");
  } else if (!${fieldName?uncap_first}.isPresent()) {
    Log.warn("Could not receive ${fieldType?uncap_first} with id " + value + " from server");
  } else {
    ${className?uncap_first}.get().remove${fieldName?cap_first}(${fieldName?uncap_first}.get());
    Log.info("Successful remove of attribute", UMLP_SHELL_API);
  }
<#else>
  if (!${className?uncap_first}.isPresent()) {
    Log.warn("Could not receive ${className?uncap_first} with id " + id + " from server");
  } else {
    ${className?uncap_first}.get().remove${fieldName?cap_first}(value);
    Log.info("Successful remove of attribute", UMLP_SHELL_API);
  }
</#if>
