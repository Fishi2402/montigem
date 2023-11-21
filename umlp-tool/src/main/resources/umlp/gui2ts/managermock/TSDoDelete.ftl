<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("public Promise<boolean> _doDelete${name}2(Long gemId, NotificationScope scope)")}
  if (!this.${name?uncap_first}s.has(gemId)) {
    return Promise.resolve(false);
  }
  let indexToRemove = this.${name?uncap_first}GemList.toArray().indexOf(this.${name?uncap_first}s.get(gemId));
  if (indexToRemove !== -1) {
    this.${name?uncap_first}GemList.toArray().splice(indexToRemove, 1);
  }
  this.${name?uncap_first}s.delete(gemId);
  this.${managerName?uncap_first}Observers.forEach(obs => obs.maybeNotify${name}Deleted(gemId, scope));
  return Promise.resolve(true);
