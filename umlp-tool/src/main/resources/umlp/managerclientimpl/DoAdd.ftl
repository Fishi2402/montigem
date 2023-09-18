<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected void _doAdd${name}(${name} o, NotificationScope scope)")}
  if(scope.contains(NotificationScope.GLOBAL)){
    StatusResult res = CommandManager.executeCommand(new ${name}_create(o)).asStatusResult();
  }
  // TODO: Error handling
  Log.trace("Adding known ${name?uncap_first} " + o.getGemId(), LogUtil.getLoggerName());
  known${name}s.put(o.getGemId(), o);
  if (!o.containsObserver(the${name}CommandObserver)) {
    o.addObserver(the${name}CommandObserver);
  }
  ${managerName?uncap_first}Observers.forEach(obs -> obs.maybeNotify${name}Added(o, scope));
