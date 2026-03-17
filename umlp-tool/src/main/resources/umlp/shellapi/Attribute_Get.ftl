<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "fieldName", "fieldType", "fieldFullType", "getterName")}
${cd4c.method("public void ${className?uncap_first}Get${fieldName?cap_first} (long id)")}
  Optional<${className}> ${className?uncap_first} = ${cdName}Manager.${className?uncap_first}Opt(id);
  if (!${className?uncap_first}.isPresent()) {
    Log.warn("Could not receive ${className?uncap_first} with id " + id + " from server");
  } else {
    ${fieldType} res = ${className?uncap_first}.get().${getterName}();
    Log.info("Successful get of attribute: " + ${cdName}BaseClassObjectMapper.serialize(res, "${fieldFullType}").orElse("(Could not serialize)"), UMLP_SHELL_API);
  }
