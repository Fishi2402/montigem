<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "type", "isGem")}
${cd4c.method("public void set${name?cap_first}(${type}  ${name}, NotificationScope scope)")}

this.${name} = Objects.requireNonNull(${name});
check${name?cap_first}();
${domain?uncap_first}Observers.forEach(obs -> obs.maybeNotifySet${name?cap_first}((${domain}) this, ${name}<#if isGem>.getGemId()</#if>, scope));