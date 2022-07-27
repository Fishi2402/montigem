<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "type")}
${cd4c.method("public void set${name?cap_first}(${type}  ${name}, NotificationScope scope)")}

if (this.${name} == null) {
this.${name} = new ${type}(${name});
  } else {
  this.${name}.set(${name});
  }

check${name?cap_first}();
${domain?uncap_first}Observers.forEach(obs -> obs.maybeNotifySet${name?cap_first}((${domain}) this, ${name}.getGemId(), scope));
