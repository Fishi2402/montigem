<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected void _doAdd${name}(${name} o, NotificationScope scope)")}
  known${name}s.put(o.getGemId(), o);
  if(!o.containsObserver(the${name}DbObserver)){
    o.addObserver(the${name}DbObserver);
  }
  if(!o.containsObserver(the${name}CommandSendingObserver)){
    o.addObserver(the${name}CommandSendingObserver);
  }
  ${managerName?uncap_first}Observers.forEach(obs -> obs.maybeNotify${name}Added(o, scope));
