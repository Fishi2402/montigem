<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName", "assocs")}
${cd4c.method("protected void _doAdd${name}(${name} o)")}

${name?uncap_first}s.put(o.getGemId(), o);
${name?uncap_first}GemList.add(o);
if (!o.containsObserver(the${name}CommandObserver)) {
  o.addObserver(the${name}CommandObserver);
}

${managerName?uncap_first}Observers.forEach(obs -> obs.notify${name}Added(o));
