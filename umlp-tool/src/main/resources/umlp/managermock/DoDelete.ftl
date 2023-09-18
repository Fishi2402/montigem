<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected boolean _doDelete${name}(Long gemId, NotificationScope scope)")}
  if (!${name?uncap_first}s.containsKey(gemId)) {
    return false;
  }
  ${name?uncap_first}GemList.remove(${name?uncap_first}s.get(gemId));
  ${name?uncap_first}s.remove(gemId);
  ${managerName?uncap_first}Observers.forEach(obs -> obs.maybeNotify${name}Deleted(gemId, scope));
  return true;
