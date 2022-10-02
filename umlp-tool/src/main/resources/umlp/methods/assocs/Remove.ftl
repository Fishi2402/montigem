<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public boolean remove${name?cap_first}(${targetName} o, NotificationScope scope)")}
if (this.${name}.remove(o)) {
  check${name?cap_first}();
  ${sourceName?uncap_first}Observers.forEach(obs -> obs.maybeNotifyRemove${name?cap_first}((${sourceName}) this, o.getGemId(), scope));
  return true;
}

return false;