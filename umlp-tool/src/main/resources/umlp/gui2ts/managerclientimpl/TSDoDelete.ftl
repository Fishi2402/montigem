<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("public Promise<Boolean> _doDelete${name}(Long gemId)")}
  return CommandManager.executeCommand(new ${name}_delete(gemId)).then(v => {
    let status = v.asStatusResult();
    if (!status.wasSuccessful()) {
      return false;
    }
    // TODO: error handling
    this.known${name}s.delete(gemId);
    this.${managerName?uncap_first}Observers.forEach(obs => obs.notify${name}Deleted(gemId));
    return true;
  });