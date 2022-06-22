<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "managerName")}
${cd4c.method("protected void _doAdd${name}(${name} o)")}

DatabaseAccessManager.add(${name}.class.getName(), o);
${managerName?uncapFirst}Observers.forEach(obs -> obs.notify${name}Added(o.getGemId()));




