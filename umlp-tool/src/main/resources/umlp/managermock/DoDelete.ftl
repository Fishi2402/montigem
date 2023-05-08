<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected boolean _doDelete${name}(Long gemId)")}
  if (!${name?uncap_first}s.containsKey(gemId)) {
    return false;
  }
  ${name?uncap_first}s.remove(gemId);
  ${name?uncap_first}GemList.remove(gemId);
  ${managerName?uncap_first}Observers.forEach(obs -> obs.notify${name}Deleted(gemId));
  return true;