<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected boolean _doDelete${name}(Long gemId, NotificationScope scope)")}
  if(!(known${name}s.containsKey(gemId) || ${name?uncap_first}DAO.select${name}(gemId).isPresent())) {
    return false;
  }
  known${name}s.remove(gemId);
  ${managerName?uncap_first}Observers.forEach(obs -> obs.notify${name}Deleted(gemId));
  return true;
