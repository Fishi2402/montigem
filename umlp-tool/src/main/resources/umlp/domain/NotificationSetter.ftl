<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "type", "name")}
${cd4c.method("public void set${name?cap_first}(${type}  o, NotificationScope scope)")}

this.${name} = o;
${domain?uncap_first}Observers.forEach(obs -> obs.maybeNotifySet${name?cap_first}((${domain}) this, o, scope));





