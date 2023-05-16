<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "type", "isGem")}
${cd4c.method("public void set${name?cap_first}(${type}  ${name}, NotificationScope scope)")}

if (_${name} == null) {
  <#-- TODO AGe: should that fail fast? -->
  console.error("0x50000 attr ${name} is null");
}
this._${name} = _${name};
this.check${name?cap_first}();
this.${domain?uncap_first}Observers.forEach(obs => obs.maybeNotifySet${name?cap_first}(<${domain}> this, _${name}<#if isGem>.getGemId()</#if>, scope));