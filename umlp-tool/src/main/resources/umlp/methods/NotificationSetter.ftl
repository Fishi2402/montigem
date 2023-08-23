<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "type", "isGem")}
${cd4c.method("public void set${name?cap_first}(${type}  ${name}, NotificationScope scope)")}

${type} oldValue = this._${name};

this._${name} = Objects.requireNonNull(_${name});
check${name?cap_first}();
${domain?uncap_first}Observers.forEach(obs -> obs.maybeNotifySet${name?cap_first}((${domain}) this, oldValue<#if isGem>.getGemId()</#if>, _${name}<#if isGem>.getGemId()</#if>, scope));
