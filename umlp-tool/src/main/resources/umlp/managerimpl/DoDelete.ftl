<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected void _doDelete${name}(${name} o)")}

known${name}s.remove(o.getGemId());
${managerName?uncap_first}Observers.forEach(obs -> obs.notify${name}Deleted(o.getGemId()));