<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected boolean _doDelete${name}(Long gemId)")}
  let status: StatusResult = CommandManager.executeCommand(new ${name}_delete(gemId)).asStatusResult();
  if (!status.wasSuccessful()) {
    return false;
  }
  // TODO: error handling
  this.known${name}s.remove(gemId);
  this.${managerName?uncap_first}Observers.forEach(obs => obs.notify${name}Deleted(gemId));
  return true;