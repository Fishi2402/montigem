<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "method")}

this._${name}.splice(index, 0, element);
this.set${name?cap_first}Present(true);
this.check${name?cap_first}();
this.${domain?uncap_first}Observers.forEach(obs => obs.maybeNotifyAdd${name?cap_first}(<${domain}> <any> this, element, index, scope));
