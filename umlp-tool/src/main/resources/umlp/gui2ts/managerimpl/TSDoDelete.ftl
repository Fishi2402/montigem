<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected boolean _doDelete${name}(Long gemId)")}
  if(!(this.known${name}s.containsKey(gemId)) {
    return false;
  }
  this.known${name}s.remove(gemId);
  this.${managerName?uncap_first}Observers.forEach(obs => obs.notify${name}Deleted(gemId));
  return true;