<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "attributeName")}
${cd4c.method("public String ${methodName}(long gemId)")}
  // TODO: remove this code, if attribute can be initialized
  if (this.${attributeName} == null) {
    this.${attributeName} = new HashMap<>();
  }
  if (this.${attributeName}.containsKey(gemId)) {
    return this.${attributeName}.get(gemId);
  }
  return "";
