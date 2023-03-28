<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected void _doDelete${name}(${name} o)")}
  StatusResult status = CommandManager.executeCommand(new ${name}_delete(o.getGemId())).asStatusResult();
  // TODO: error handling
  known${name}s.remove(o.getGemId());
  ${managerName?uncap_first}Observers.forEach(obs -> obs.notify${name}Deleted(o));