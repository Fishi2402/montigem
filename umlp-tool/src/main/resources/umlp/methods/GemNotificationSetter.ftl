<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "type")}
${cd4c.method("public void set${name?cap_first}(${type}  ${name}, NotificationScope scope)")}

long oldValue;

if (this._${name} == null) {
  this._${name} = new ${type}(_${name});
  oldValue = -1;
} else {
  oldValue = this._${name}.getGemId();
  this._${name}.set(_${name});
  }
set${name?cap_first}Present(true);
check${name?cap_first}();
${domain?uncap_first}Observers.forEach(obs -> obs.maybeNotifySet${name?cap_first}((${domain}) this, oldValue, _${name}.getGemId(), scope));
