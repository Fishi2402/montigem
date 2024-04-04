<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "type")}
${cd4c.method("public void set${name?cap_first}(${type}  ${name}, NotificationScope scope)")}

_internal_set${name?cap_first}(_${name}, scope);
set${name?cap_first}Present(true);
check${name?cap_first}();
