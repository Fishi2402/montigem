<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("public boolean _doDelete${name}2(Long gemId, NotificationScope scope)")}
  if(!(this.known${name}s.containsKey(gemId)) {
    return false;
  }
  this.known${name}s.remove(gemId);
  this.${managerName?uncap_first}Observers.forEach(obs => obs.maybeNotify${name}Deleted(gemId, scope));
  return true;
