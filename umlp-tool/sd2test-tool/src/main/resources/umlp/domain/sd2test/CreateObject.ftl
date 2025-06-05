<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "cdName")}
${cd4c.method("public ${type} create${name?cap_first}()")}

return ${cdName}Manager.${type?uncap_first}Builder().build().orElseThrow();


