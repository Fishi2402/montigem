<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected void _doAdd${name}(${name} o)")}
  known${name}s.put(o.getGemId(), o);
  if(!o.containsObserver(the${name}DbObserver)){
    o.addObserver(the${name}DbObserver);
  }
  ${managerName?uncap_first}Observers.forEach(obs -> obs.notify${name}Added(o));
