<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected boolean _doDelete${name}(Long gemId, NotificationScope scope)")}
  if(scope.contains(NotificationScope.GLOBAL)){
    StatusResult status = CommandManager.executeCommand(new ${name}_delete(gemId)).asStatusResult();
    if (!status.wasSuccessful()) {
      return false;
    }
  }
  // TODO: error handling
  known${name}s.remove(gemId);
  if (this.loaded${name}List.isPresent()) {
    this.loaded${name}List.get().removeIdSure(gemId);
  }
  ${managerName?uncap_first}Observers.forEach(obs -> obs.notify${name}Deleted(gemId));
  return true;
