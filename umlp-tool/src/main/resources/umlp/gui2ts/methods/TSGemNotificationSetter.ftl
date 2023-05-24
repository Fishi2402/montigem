<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "type")}
${cd4c.method("public void set${name?cap_first}(${type}  ${name}, NotificationScope scope)")}

if (this._${name} == null) {
  this._${name} = new ${type}().Gem2(_${name});
} else {
  this._${name}.set(_${name});
}

this.check${name?cap_first}();
this.${domain?uncap_first}Observers.forEach(obs => obs.maybeNotifySet${name?cap_first}(<${domain}> this, _${name}.getGemId(), scope));