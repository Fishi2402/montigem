<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected void _doDelete${name}(${name} o)")}

assert ${name?uncap_first}s.containsKey(o.getGemId());
${name?uncap_first}s.remove(o.getGemId(), o);
${name?uncap_first}GemList.remove(o);

${managerName?uncap_first}Observers.forEach(obs -> obs.notify${name}Deleted(o.getGemId()));