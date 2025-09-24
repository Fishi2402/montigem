<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "initializationTypeName", "fieldName", "fieldType", "fieldFullType", "getterName")}
${cd4c.method("public void ${initializationTypeName?uncap_first}Get${fieldName?cap_first} (long id)")}
  Optional<${initializationTypeName}> ${initializationTypeName?uncap_first} = ${cdName}Manager.${initializationTypeName?uncap_first}Opt(id);
  if (!${initializationTypeName?uncap_first}.isPresent()) {
    Log.warn("Could not receive ${initializationTypeName?uncap_first} with id " + id + " from server");
  } else {
    ${fieldType} res = ${initializationTypeName?uncap_first}.get().${getterName}();
    Log.info("Successful get of attribute: " + ${cdName}BaseClassObjectMapper.serialize(res, "${fieldFullType}").orElse("(Could not serialize)"), UMLP_SHELL_API);
  }
