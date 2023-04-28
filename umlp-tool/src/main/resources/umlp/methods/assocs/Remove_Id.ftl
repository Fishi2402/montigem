<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public ${targetName} remove${name?cap_first}(int idx, NotificationScope scope)")}
${targetName} c = _${name}.get(idx);
_${name}._internal_remove(Collections.singleton(idx));
${sourceName?uncap_first}Observers.forEach(obs -> obs.maybeNotifyRemove${name?cap_first}((${sourceName}) this, c.getGemId(), idx, scope));
return c;
