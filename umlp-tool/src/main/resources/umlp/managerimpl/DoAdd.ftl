<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected void _doAdd${name}(${name} o)")}

<#-- TODO: impl -->
${managerName?uncap_first}Observers.forEach(obs -> obs.notify${name}Added(o.getGemId()));