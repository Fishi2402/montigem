<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public ${targetName} remove${name?cap_first}(int idx, NotificationScope scope)")}
${targetName} c = ${name}.get(idx);
${name}._internal_remove(Collections.singleton(idx));
${sourceName?uncap_first}Observers.forEach(obs -> obs.maybeNotifyRemove${name?cap_first}((${sourceName}) this, c.getGemId(), scope));
return c;
