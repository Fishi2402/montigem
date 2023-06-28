<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("public void _doAdd${name}(${name} o)")}
  this.known${name}s.set(o.getGemId(), o);
  this.${managerName?uncap_first}Observers.forEach(obs => obs.notify${name}Added(o));
