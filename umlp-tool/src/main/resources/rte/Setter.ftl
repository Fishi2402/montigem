<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type")}
${cd4c.method("public void set${name?cap_first}(${type}  ${name?uncap_first})")}

this._${name?uncap_first} = Objects.requireNonNull(_${name?uncap_first});
set${name?cap_first}Present(true);
