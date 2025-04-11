<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "type")}
${cd4c.method("public void set${name?cap_first}(${type}  ${name}, NotificationScope scope)")}

this._internal_set${name?cap_first}2(_${name}, scope);
this.set${name?cap_first}Present(true);
this.check${name?cap_first}();

